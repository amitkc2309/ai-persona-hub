package com.ai.persona.profiles_conversation.factory;

import com.ai.persona.profiles_conversation.dto.AiRequest;
import com.ai.persona.profiles_conversation.enums.AiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatClientFactory {

    @Qualifier("ollamaChatClient")
    private final ChatClient ollamaChatClient;

    @Value("${application.default-ai-provider}")
    private AiProvider defaultAiProvider;

    @Qualifier("openAiChatClient")
    private final ChatClient openAiChatClient;

    public ChatClient getChatClient(AiRequest request) {
        AiProvider aiProvider = request.getAiProvider() != null ? request.getAiProvider() : defaultAiProvider;
        return switch (aiProvider) {
            case OLLAMA -> ollamaChatClient;
            case OPENAI -> openAiChatClient;
        };
    }
}
