package com.ai.persona.profiles_conversation.dto;

import com.ai.persona.profiles_conversation.enums.AiProvider;
import lombok.Data;

@Data
public class AiRequest {
    private AiProvider aiProvider;
    private String modelName;
}
