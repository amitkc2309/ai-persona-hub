package com.ai.persona.profiles_conversation.repository;

import com.ai.persona.profiles_conversation.entity.Conversation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ConversationRepository extends ReactiveMongoRepository<Conversation,String> {
    Mono<Conversation> findByProfile1AndProfile2OrProfile2AndProfile1(String profile1, String profile2, String profile2Again, String profile1Again);
}
