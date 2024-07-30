package com.ai.persona.profiles_conversation.repository;

import com.ai.persona.profiles_conversation.entity.Profile;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile,String> {
    Mono<Profile> findByEmail(String email);
    @Aggregation(pipeline = {
            "{ $match: { gender: ?0 } }",
            "{ $sample: { size: 1 } }"
    })
    Mono<Profile> getRandomProfileByGender(String gender);

    @Aggregation(pipeline = {
            "{ $sample: { size: 1 } }"
    })
    Mono<Profile> getRandomProfile();
}
