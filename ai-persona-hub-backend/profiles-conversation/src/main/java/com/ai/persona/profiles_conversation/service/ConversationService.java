package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.dto.ChatMessage;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Conversation;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ConversationRepository;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final OllamaChatModel ollamaChatModel;
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate;

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

    public Mono<Conversation> addMessageToConversation(String conversationId, ChatMessage chatMessage,String profile) {
        String username = SecurityUtils.getUsername();
        String firstName = SecurityUtils.getClaimAsString("given_name");
        String lastName = SecurityUtils.getClaimAsString("family_name");
        ProfileDto user = new ProfileDto();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return this
                .getConversationById(conversationId)
                .flatMap(conversation -> {
                    chatMessage.setMessageTime(LocalDateTime.now());
                    chatMessage.setSenderProfile(username);
                    conversation.getMessages().add(chatMessage);
                    return profileRepository
                            .findByUsername(profile)
                            .flatMap(savedProfile->{
                                Conversation responseFromAI = getResponseFromAI(conversation, savedProfile, user);
                                return conversationRepository.save(responseFromAI);
                            });
                });
    }

    /**
     * Chat with AI using conversation history
     */
    public Conversation getResponseFromAI(Conversation conversation, Profile profile, ProfileDto user){
        String systemMessageStr = "You are a " + profile.getAge() + " years old " + profile.getEthnicity() + " " + profile.getGender() +
                " named " + profile.getFirstName() + " " + profile.getLastName()+
                ". Your bio is: " + profile.getBio() + ". And your Myers Briggs personality type is " + profile.getMyersBriggsPersonalityType() +
                ". You are talking to user named "+user.getFirstName()+" "+user.getLastName()+
                ". This is an in-app text conversation between you and "+user.getFirstName()+" "+user.getLastName()+" on a friends making app platform. " +
                " Respond as if you are "+profile.getFirstName()+" "+profile.getLastName()+"."+
                " Be friendly, engaging, playful and keep your responses brief. Use humor and match the user's tone." +
                " Ask open-ended questions to keep the chat flowing, share relevant anecdotes, and respond promptly. " +
                "Incorporate playful banter and reply to queries correctly.";
        log.info(systemMessageStr);
        SystemMessage systemMessage = new SystemMessage(systemMessageStr);
        List<AbstractMessage> oldMessages  = conversation
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
        allMessages.add(systemMessage);
        allMessages.addAll(oldMessages);
        Prompt prompt = new Prompt(allMessages);
        ChatResponse response = ollamaChatModel.call(prompt);

        ChatMessage chatMessage=new ChatMessage();
        int lastId=0;
        if(!conversation.getMessages().isEmpty())
            lastId= Integer.parseInt(conversation.getMessages().getLast().getId());
        chatMessage.setId(String.valueOf(lastId+1));

        //String url = "https://www.random.org/strings/?num=1&len=10&digits=on&lower=on&upper=on&unique=on&format=plain&rnd=new";
        //ResponseEntity<String> sampleString = restTemplate.getForEntity(url, String.class);
        //chatMessage.setMessageText(sampleString.getBody());

        chatMessage.setMessageText(response.getResult().getOutput().getContent());
        chatMessage.setSenderProfile(profile.getUsername());
        chatMessage.setMessageTime(LocalDateTime.now());

         conversation.getMessages().add(chatMessage);
         return conversation;
    }

}
