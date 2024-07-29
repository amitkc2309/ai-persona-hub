package com.ai.persona.profiles_conversation.dto;

import com.ai.persona.profiles_conversation.entity.ChatMessage;
import lombok.Data;

import java.util.List;

@Data
public class ConversationDto {
    String id;
    String profile1;
    String profile2;
    List<ChatMessage> messages;
}
