package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.enums.AiProvider;
import com.ai.persona.profiles_conversation.service.AiModelsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class AppConfigController {

    private final AiModelsService aiModelsService;

    @GetMapping("/ai-models")
    public Map<AiProvider, List<String>> getAiModels() {
        return aiModelsService.getAiModels();
    }

}
