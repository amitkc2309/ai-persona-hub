package com.ai.persona.profiles_conversation.repository;

import com.ai.persona.profiles_conversation.entity.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProfileRepoPaginationImpl implements ProfileRepoPagination{

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Flux<Profile> findAllByIdWithPagination(Set<String> ids, int page, int size) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.match(Criteria.where("_id").in(ids)));

        //Apply `skip` and `limit` for pagination
        operations.add(Aggregation.skip((long) page * size));
        operations.add(Aggregation.limit(size));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        return Flux.from(reactiveMongoTemplate.aggregate(aggregation, "profile", Profile.class));
    }
}
