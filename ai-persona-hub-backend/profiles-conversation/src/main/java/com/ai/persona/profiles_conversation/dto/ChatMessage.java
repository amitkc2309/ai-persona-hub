package com.ai.persona.profiles_conversation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    String id;
    String messageText;
    String senderProfile;
    LocalDateTime messageTime;
}
