package com.ai.persona.profiles_conversation.repository;

import com.ai.persona.profiles_conversation.entity.Profile;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile,String>,ProfileRepoPagination {
    Mono<Profile> findByEmail(String email);

    Flux<Profile> findAllById(Set<String> ids);

    Mono<Profile> findByUsername(String username);
    @Aggregation(pipeline = {
            "{ $match: { gender: ?0, isBot: true } }",
            "{ $sample: { size: 1 } }"
    })
    Mono<Profile> getRandomBotProfileByGender(String gender);

    @Aggregation(pipeline = {
            "{ $match: { gender: ?0, isBot: true, _id: { $ne: ?1 } } }",
            "{ $sample: { size: 1 } }"
    })
    Mono<Profile> getRandomBotProfileByGenderExceptLastRandom(String gender,String excludeLastRandomProfileId);

    @Aggregation(pipeline = {
            "{ $match: { isBot: true } }",
            "{ $sample: { size: 1 } }"
    })
    Mono<Profile> getRandomBotProfile();

    @Aggregation(pipeline = {
            "{ $match: { isBot: true, _id: { $ne: ?0 } } }",
            "{ $sample: { size: 1 } }"
    })
    Mono<Profile> getRandomBotProfileExceptLastRandom(String excludeLastRandomProfileId);

    @Query("{ 'isBot': true }")
    Flux<Profile> findAllBots();
}
