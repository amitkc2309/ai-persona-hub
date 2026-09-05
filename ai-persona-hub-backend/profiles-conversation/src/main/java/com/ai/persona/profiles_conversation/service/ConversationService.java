package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.dto.AiRequest;
import com.ai.persona.profiles_conversation.dto.ChatMessage;
import com.ai.persona.profiles_conversation.dto.ChatMessageDto;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Conversation;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.enums.AiProvider;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.factory.ChatClientFactory;
import com.ai.persona.profiles_conversation.factory.ChatOptionsFactory;
import com.ai.persona.profiles_conversation.repository.ConversationRepository;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ProfileRepository profileRepository;
    private final ChatClientFactory chatClientFactory;
    private final ChatOptionsFactory chatOptionsFactory;
    @Value("classpath:/templates/SystemPromptTemplate.st")
    Resource systemPrompt;
    @Value("classpath:/templates/UserPromptTemplate.st")
    Resource userPrompt;

    public Mono<Conversation> getConversationById(String conversationId) {
        return conversationRepository
                .findById(conversationId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("conversationId", conversationId)));
    }

    public Mono<Conversation> getOrCreateNewConversation(String profile1, String profile2) {
        return conversationRepository
                .findByProfile1AndProfile2OrProfile2AndProfile1(profile1, profile2, profile2, profile1)
                .switchIfEmpty(createNewConversation(profile1, profile2));
    }

    private Mono<Conversation> createNewConversation(String profile1, String profile2) {
        Conversation conversation = new Conversation();
        conversation.setProfile1(profile1);
        conversation.setProfile2(profile2);
        conversation.setMessages(new ArrayList<>());
        return conversationRepository.save(conversation);
    }

    /*
     * NOTE:
     * We are getting username/claims here (outside flatMap/map) because inside the reactive
     * chain (map/flatMap etc.) SecurityUtils.getUsername() will NOT work as SecurityContextHolder
     * does not work inside reactive chain.
     *
     * That’s why we grab it up front before entering the Flux/Mono pipeline.
     *
     * Correct fix (to do later): Use ReactiveSecurityContextHolder so that we can
     * safely fetch username/claims INSIDE the reactive chain.
     */
    public Flux<String> addMessageToConversation(String conversationId, ChatMessageDto chatMessageDto, String profile) {
        ChatMessage userInputMsg = new ChatMessage();
        BeanUtils.copyProperties(chatMessageDto, userInputMsg);
        String username = SecurityUtils.getUsername();
        String firstName = SecurityUtils.getClaimAsString("given_name");
        String lastName = SecurityUtils.getClaimAsString("family_name");
        ProfileDto user = new ProfileDto();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return this
                .getConversationById(conversationId)
                .flatMapMany(conversation -> {
                    userInputMsg.setMessageTime(LocalDateTime.now());
                    userInputMsg.setSenderProfile(username);
                    conversation.getMessages().add(userInputMsg);
                    return profileRepository
                            .findByUsername(profile)
                            .flatMapMany(savedProfile -> {
                                ChatMessage aiResponseMsg=new ChatMessage();
                                int lastId=0;
                                if(!conversation.getMessages().isEmpty())
                                    lastId= Integer.parseInt(conversation.getMessages().getLast().getId());
                                aiResponseMsg.setId(String.valueOf(lastId+1));
                                aiResponseMsg.setMessageText("");
                                aiResponseMsg.setSenderProfile(savedProfile.getUsername());
                                aiResponseMsg.setMessageTime(LocalDateTime.now());
                                return getResponseFromAI(conversation, savedProfile, user, chatMessageDto.getAiRequest())
                                        .doOnNext(part -> {
                                            aiResponseMsg.setMessageText(aiResponseMsg.getMessageText()+part);
                                        })
                                        //.map(part -> chatMessage2)
                                        // and save it after completion
                                        .doFinally(signalType -> {
                                            conversation.getMessages().add(aiResponseMsg);
                                            conversationRepository.save(conversation).subscribe();
                                        });
                            });
                });
    }

    /**
     * Chat with AI using conversation history
     */
    public Flux<String> getResponseFromAI(Conversation conversation, Profile profile, ProfileDto user, AiRequest aiRequest) {
        List<? extends AbstractMessage> oldMessages = conversation
                .getMessages()
                .stream()
                .map(message -> {
                    if (message.getSenderProfile().equals(profile.getUsername())) {
                        return new AssistantMessage(message.getMessageText());
                    } else {
                        return new UserMessage(message.getMessageText());
                    }
                }).toList();
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(oldMessages);
        Prompt prompt = new Prompt(allMessages);
        ChatClient chatClient = chatClientFactory.getChatClient(aiRequest);
        return chatClient.prompt(prompt)
                .options(chatOptionsFactory.buildChatOptions(aiRequest))
                .system(promptUserSpec ->
                        promptUserSpec
                                .text(systemPrompt)
                                .param("profileAge", profile.getAge())
                                .param("profileEthnicity", profile.getEthnicity())
                                .param("profileGender", profile.getGender())
                                .param("profileFirstName", profile.getFirstName())
                                .param("profileLastName", profile.getLastName())
                                .param("profileBio", profile.getBio())
                                .param("profileMyersBriggsType", profile.getMyersBriggsPersonalityType())
                                .param("userFirstName", user.getFirstName())
                                .param("userLastName", user.getLastName()))
                .stream().content();
    }

    public Flux<String> testAIStream(ChatMessageDto chatMessageDto){
        ChatClient chatClient = chatClientFactory.getChatClient(chatMessageDto.getAiRequest());
        return chatClient.prompt()
                .options(chatOptionsFactory.buildChatOptions(chatMessageDto.getAiRequest()))
                .user(promptUserSpec ->
                        promptUserSpec
                                .text(userPrompt)
                                .param("userFirstName", "John")
                                .param("userLastName", "Smith")
                                .param("userMessage", chatMessageDto.getMessageText())
                )
                .stream()
                .content();
    }
}
