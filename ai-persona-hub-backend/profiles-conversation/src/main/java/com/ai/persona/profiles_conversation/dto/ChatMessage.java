package com.ai.persona.profiles_conversation.dto;

import com.ai.persona.profiles_conversation.enums.AiProvider;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    String id;
    String messageText;
    String senderProfile;
    LocalDateTime messageTime;
}
