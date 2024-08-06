package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.dto.ConversationDto;
import com.ai.persona.profiles_conversation.dto.ChatMessage;
import com.ai.persona.profiles_conversation.service.ConversationService;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping(("/{conversationId}"))
    public Mono<ResponseEntity<ConversationDto>> getConversationById(@PathVariable String conversationId) {
        return conversationService
                .getConversationById(conversationId)
                .map(conversation -> {
                    ConversationDto conversationDto = new ConversationDto();
                    BeanUtils.copyProperties(conversation, conversationDto);
                    return ResponseEntity.ok(conversationDto);
                });
    }

    @GetMapping("/fetch")
    public Mono<ResponseEntity<ConversationDto>> getOrCreateNewConversation(@RequestParam String profile) {
        return conversationService
                .getOrCreateNewConversation(SecurityUtils.getUsername(),profile)
                .map(conversation -> {
                    ConversationDto conversationDto = new ConversationDto();
                    BeanUtils.copyProperties(conversation, conversationDto);
                    return ResponseEntity.ok(conversationDto);
                });
    }

    @PutMapping(("/{conversationId}"))
    public Mono<ResponseEntity<ChatMessage>> addMessageToConversation(@PathVariable String conversationId,
                                                               @RequestBody ChatMessage chatMessage,
                                                               @RequestParam String profile) {
        return conversationService
                .addMessageToConversation(conversationId,chatMessage,profile)
                .map(conversation -> {
                    return ResponseEntity.ok(conversation.getMessages().getLast());
                });
    }
}
