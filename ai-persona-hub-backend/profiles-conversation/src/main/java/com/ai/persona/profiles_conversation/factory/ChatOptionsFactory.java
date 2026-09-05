package com.ai.persona.profiles_conversation.factory;

import com.ai.persona.profiles_conversation.dto.AiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatOptionsFactory {

    public ChatOptions buildChatOptions(AiRequest request) {
        return switch (request.getAiProvider()) {
            case OLLAMA -> OllamaChatOptions.builder()
                    .model(request.getModelName())
                    .temperature(0.7)
                    .disableThinking()
                    .build();

            case OPENAI -> OpenAiChatOptions.builder()
                    .model(request.getModelName())
                    .build();
        };
    }
}
