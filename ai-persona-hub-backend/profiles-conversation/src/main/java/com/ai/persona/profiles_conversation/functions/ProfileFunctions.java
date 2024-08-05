package com.ai.persona.profiles_conversation.functions;

import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.dto.ProfileDtoGenerated;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;

/**
 * AI returns Response as String so this is the way we make AI return response inside a DTO
 */
@Configuration
@Log
@RequiredArgsConstructor
public class ProfileFunctions {

    private final ProfileService profileService;

    @Bean
    @Description("Save the generated profile information")
    public Function<ProfileDtoGenerated, Mono<ProfileDto>> saveGeneratedProfile() {
        return (ProfileDtoGenerated generated) -> {
            ProfileDto profileDto = new ProfileDto();
            BeanUtils.copyProperties(generated,profileDto);
            profileDto.setIsBot(Boolean.TRUE);
            profileDto.setMatchedProfiles(new HashSet<>());
            profileDto.setImageUrls(null);
            profileDto.setUsername(UUID.randomUUID().toString());
            log.info("*****saving profile "+profileDto.toString());
            return profileService
                    .saveProfile(profileDto)
                    .doOnSuccess(saved -> {
                        Mono.fromCallable(() -> profileService.generateAndSaveImage(saved))
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe();
                    })
                    .then(Mono.just(profileDto));
        };
    }
}
