package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.dto.ConversationDto;
import com.ai.persona.profiles_conversation.dto.ChatMessage;
import com.ai.persona.profiles_conversation.service.ConversationService;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
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

    /**
     * produces = MediaType.TEXT_EVENT_STREAM_VALUE is must
     * @return
     */
    @PutMapping(value = "/{conversationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> addMessageToConversation(@PathVariable String conversationId,
                                                               @RequestBody ChatMessage chatMessage,
                                                               @RequestParam String profile) {
        return conversationService
                .addMessageToConversation(conversationId,chatMessage,profile);
    }

    @GetMapping("/test-ai")
    public ResponseEntity<String> testAI(@RequestParam String prompt) {
        String chatResponse = conversationService.testAI(prompt);
        return ResponseEntity.ok().body(chatResponse);
    }

    /**
     * Streaming can be seen on browser.
     */

    @GetMapping(value = "/test-ai-stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> testAIStream(@RequestParam String prompt) {
        return conversationService
                .testAIStream(prompt);
    }

}
