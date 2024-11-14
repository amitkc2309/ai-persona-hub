package com.ai.persona.profiles_conversation.unit_test;

import com.ai.persona.profiles_conversation.dto.ChatMessage;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Conversation;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ConversationRepository;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.service.ConversationService;
import com.ai.persona.profiles_conversation.service.ProfileService;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversationServiceTest {

    @InjectMocks
    private ConversationService conversationService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private OllamaChatModel ollamaChatModel;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private Conversation conversation;

    @BeforeEach
    public void setup() {
        conversation = new Conversation();
        conversation.setMessages(new ArrayList<>());
    }

    @Test
    public void testGetConversationById_NotFound() {
        // Mock behavior to throw error if no conversation is found
        String conversationId = "nonexistentId";
        when(conversationRepository.findById(conversationId)).thenReturn(Mono.empty());

        // Verify exception is thrown for missing conversation
        assertThrows(ResourceNotFoundException.class, () ->
                conversationService.getConversationById(conversationId).block());
    }

    @Test
    public void testGetOrCreateNewConversation_NewConversation_using_any() {
        String profile1 = "user1";
        String profile2 = "user2";
        conversation.setProfile1(profile1);
        conversation.setProfile2(profile2);

        when(conversationRepository
                .findByProfile1AndProfile2OrProfile2AndProfile1(profile1, profile2, profile2, profile1))
                .thenReturn(Mono.empty());

        /*
        createNewConversation() is private so this is out of option
        when(conversationService.createNewConversation(profile1,profile2))
                .thenReturn(Mono.just(conversation));
        */
        // ***********  So Instead we use any()
        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(Mono.just(conversation));

        StepVerifier.create(conversationService.getOrCreateNewConversation(profile1, profile2))
                .assertNext(savedConversation -> {
                    assertThat(savedConversation.getProfile1()).isEqualTo("user1");
                    assertThat(savedConversation.getProfile2()).isEqualTo("user2");
                })
                .verifyComplete();
    }
}
