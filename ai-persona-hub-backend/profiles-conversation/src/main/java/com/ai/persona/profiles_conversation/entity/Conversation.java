package com.ai.persona.profiles_conversation.entity;

import com.ai.persona.profiles_conversation.dto.ChatMessage;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Conversation {
    @Id
    String id;
    String profile1;
    String profile2;
    List<ChatMessage> messages;
}
