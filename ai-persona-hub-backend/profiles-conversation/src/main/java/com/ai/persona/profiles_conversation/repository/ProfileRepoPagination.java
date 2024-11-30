package com.ai.persona.profiles_conversation.repository;

import com.ai.persona.profiles_conversation.entity.Profile;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface ProfileRepoPagination {
    Flux<Profile> findAllByIdWithPagination(Set<String> ids, int page, int size);
}
