package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.entity.ChatMessage;
import com.ai.persona.profiles_conversation.entity.Conversation;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Mono<Conversation> getConversationById(String conversationId) {
        return conversationRepository
                .findById(conversationId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("conversationId", conversationId)));
    }

    public Mono<Conversation> getConversationByProfiles(String profile1, String profile2) {
        return conversationRepository
                .findByProfile1AndProfile2OrProfile2AndProfile1(profile1, profile2, profile2, profile1)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("conversation", profile1 + " & " + profile2)));
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

    public Mono<Conversation> addMessageToConversation(String conversationId, ChatMessage chatMessage) {
        return this
                .getConversationById(conversationId)
                .flatMap(conversation -> {
                    chatMessage.setId(UUID.randomUUID().toString());
                    chatMessage.setMessageTime(LocalDateTime.now());
                    conversation.getMessages().add(chatMessage);
                    return conversationRepository.save(conversation);
                });
    }

}
