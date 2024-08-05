package com.ai.persona.profiles_conversation.repository;

import com.ai.persona.profiles_conversation.dto.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage,String> {
}
