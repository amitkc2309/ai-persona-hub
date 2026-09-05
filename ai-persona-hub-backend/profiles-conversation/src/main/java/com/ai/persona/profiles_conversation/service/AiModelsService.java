package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.configuration.AiModelsProperties;
import com.ai.persona.profiles_conversation.enums.AiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiModelsService {

    private final AiModelsProperties aiModelsProperties;

    public Map<AiProvider, List<String>> getAiModels() {
        return aiModelsProperties.getModels();
    }
}
