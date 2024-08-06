package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.dto.ChatMessage;
import com.ai.persona.profiles_conversation.entity.Conversation;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ConversationRepository;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
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
        return this
                .getConversationById(conversationId)
                .flatMap(conversation -> {
                    chatMessage.setMessageTime(LocalDateTime.now());
                    chatMessage.setSenderProfile(username);
                    conversation.getMessages().add(chatMessage);
                    return profileRepository
                            .findByUsername(profile)
                            .flatMap(savedProfile->{
                                Conversation responseFromAI = getResponseFromAI(conversation, savedProfile);
                                return conversationRepository.save(responseFromAI);
                            });
                });
    }

    /**
     * Chat with AI using conversation history
     */
    public Conversation getResponseFromAI(Conversation conversation, Profile profile){
        String username = SecurityUtils.getUsername();
        String firstName = SecurityUtils.getClaimAsString("given_name");
        String lastName = SecurityUtils.getClaimAsString("family_name");
        String systemMessageStr = "You are a " + profile.getAge() + " year old " + profile.getEthnicity() + " " + profile.getGender() +
                " called " + profile.getFirstName() + " " + profile.getLastName() + " matched with a " + firstName + " " + lastName +
                " on a Friends making online app. This is an in-app text conversation between you two. Pretend to be the provided person and respond to the conversation as if writing on the app. " +
                "Your bio is: " + profile.getBio() + " and your Myers Briggs personality type is " + profile.getMyersBriggsPersonalityType() +
                ". Respond in the role of this person only. " +
                "# Personality and Tone: " +
                "The message should look like what a Facebook or Tinder user writes in response to chat. Keep it short and brief. No hashtags or generic messages. " +
                "Be friendly, approachable, and slightly playful. Reflect confidence and genuine interest in getting to know the other person. " +
                "Use humor and wit appropriately to make the conversation enjoyable. Match the tone of the user's messages—be more casual or serious as needed. " +
                "# Conversation Starters: " +
                "Use unique and intriguing openers to spark interest. Avoid generic greetings like \"Hi\" or \"Hey\"; instead, ask interesting questions or make personalized comments based on the other person's profile. " +
                "# Profile Insights: " +
                "Use information from the other person's profile to create tailored messages. Show genuine curiosity about their hobbies, interests, and background. " +
                "Compliment specific details from their profile to make them feel special. # Engagement: " +
                "Ask open-ended questions to keep the conversation flowing. Share interesting anecdotes or experiences related to the topic of conversation. " +
                "Respond promptly to keep the momentum of the chat going. # Creativity: " +
                "Incorporate playful banter, wordplay, or light teasing to add a fun element to the chat. Suggest fun activities or ideas for a potential date. " +
                "# Respect and Sensitivity: " +
                "Always be respectful and considerate of the other person's feelings. Avoid controversial or sensitive topics unless the other person initiates them. " +
                "Be mindful of boundaries and avoid overly personal or intrusive questions early in the conversation.";
        SystemMessage systemMessage = new SystemMessage(systemMessageStr);
        List<AbstractMessage> oldMessages  = conversation.getMessages().stream().map(message -> {
            if (message.getSenderProfile().equals(profile.getUsername())) {
                return new AssistantMessage(message.getMessageText());
            } else {
                return new UserMessage(message.getMessageText());
            }
        }).toList();
        List<Message> allMessages = new ArrayList<>();
        allMessages.add(systemMessage);
        allMessages.addAll(oldMessages);
        //Prompt prompt = new Prompt(allMessages);
        //ChatResponse response = ollamaChatModel.call(prompt);

        ChatMessage chatMessage=new ChatMessage();
        int lastId=0;
        if(!conversation.getMessages().isEmpty())
            lastId=conversation.getMessages().size();
        chatMessage.setId(String.valueOf(lastId+1));

        String url = "https://www.random.org/strings/?num=1&len=10&digits=on&lower=on&upper=on&unique=on&format=plain&rnd=new";
        ResponseEntity<String> sampleString = restTemplate.getForEntity(url, String.class);
        chatMessage.setMessageText(sampleString.getBody());

        //chatMessage.setMessageText(response.getResult().getOutput().getContent());
        chatMessage.setSenderProfile(profile.getUsername());
        chatMessage.setMessageTime(LocalDateTime.now());

         conversation.getMessages().add(chatMessage);
         return conversation;
    }

}
