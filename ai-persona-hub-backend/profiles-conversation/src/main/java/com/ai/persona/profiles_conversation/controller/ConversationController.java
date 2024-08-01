package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.dto.ConversationDto;
import com.ai.persona.profiles_conversation.entity.ChatMessage;
import com.ai.persona.profiles_conversation.service.ConversationService;
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

    @GetMapping
    public Mono<ResponseEntity<ConversationDto>> getConversationByProfiles(@RequestParam String profile1, @RequestParam String profile2) {
        return conversationService
                .getConversationByProfiles(profile1, profile2)
                .map(conversation -> {
                    ConversationDto conversationDto = new ConversationDto();
                    BeanUtils.copyProperties(conversation, conversationDto);
                    return ResponseEntity.ok(conversationDto);
                });
    }

    @GetMapping("/fetch")
    public Mono<ResponseEntity<ConversationDto>> getOrCreateNewConversation(@RequestParam String profile1, @RequestParam String profile2) {
        return conversationService
                .getOrCreateNewConversation(profile1, profile2)
                .map(conversation -> {
                    ConversationDto conversationDto = new ConversationDto();
                    BeanUtils.copyProperties(conversation, conversationDto);
                    return ResponseEntity.ok(conversationDto);
                });
    }

    @PutMapping(("/{conversationId}"))
    public Mono<ResponseEntity<Void>> addMessageToConversation(@PathVariable String conversationId, @RequestBody ChatMessage chatMessage) {
        return conversationService
                .addMessageToConversation(conversationId, chatMessage)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }
}
