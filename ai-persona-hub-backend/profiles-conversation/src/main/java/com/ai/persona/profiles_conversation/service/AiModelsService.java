package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.configuration.AiModelsProperties;
import com.ai.persona.profiles_conversation.constants.Gender;
import com.ai.persona.profiles_conversation.dto.AiRequest;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.dto.ProfileDtoGenerated;
import com.ai.persona.profiles_conversation.enums.AiProvider;
import com.ai.persona.profiles_conversation.factory.ChatClientFactory;
import com.ai.persona.profiles_conversation.factory.ChatOptionsFactory;
import com.ai.persona.profiles_conversation.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Log
@RequiredArgsConstructor
public class AiModelsService {

    private final AiModelsProperties aiModelsProperties;

    private final ChatClientFactory chatClientFactory;
    private final ChatOptionsFactory chatOptionsFactory;
    @Value("classpath:/templates/GenerateRandomBotProfile.st")
    Resource randomBotProfilePrompt;
    private final ImageGenerationService imageGenerationService;
    private final ProfileService profileService;

    public Map<AiProvider, List<String>> getAiModels() {
        return aiModelsProperties.getModels();
    }

    public Mono<ProfileDto> generateRandomBotProfile(Gender gender, Integer age, String ethnicity, AiRequest aiRequest) {
        int randomAge = age != null ? age : CommonUtility.getRandomAge();
        Gender randomGender = gender != null ? gender : CommonUtility.getRandomGender();
        String personalityType = CommonUtility.getPersonalityTypes();
        String randomEthnicity = ethnicity != null ? ethnicity : CommonUtility.getRandomEthnicity();
        ChatClient chatClient = chatClientFactory.getChatClient(aiRequest);
        return Mono.fromCallable(() ->
                        chatClient.prompt()
                                .options(chatOptionsFactory.buildChatOptions(aiRequest))
                                .user(promptUserSpec -> promptUserSpec
                                        .text(randomBotProfilePrompt)
                                        .param("age", randomAge)
                                        .param("personalityType", personalityType)
                                        .param("ethnicity", randomEthnicity)
                                        .param("gender", randomGender)
                                )
                                .call()
                                .entity(ProfileDtoGenerated.class)
                )
                .map(generated -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(generated, profileDto);
                    profileDto.setIsBot(Boolean.TRUE);
                    profileDto.setMatchedProfiles(new HashSet<>());
                    profileDto.setImageUrls(null);
                    profileDto.setUsername(UUID.randomUUID().toString());
                    return profileDto;
                })
                // 1. Save profile entity in DB
                .flatMap(profileService::saveProfile)
                // 2. Fire-and-forget image generation asynchronously
                .doOnNext(savedProfile ->
                        imageGenerationService.generateAndSaveImage(savedProfile)
                                .subscribeOn(Schedulers.boundedElastic())
                                .doOnSuccess(img -> log.info("Background image generation completed for user: " + savedProfile.getUsername()))
                                .doOnError(err -> log.severe("Background image generation failed for user: " + savedProfile.getUsername() + " with Error: " + err))
                                .subscribe()
                )
                // 3. Map saved Entity to final ProfileDto to emit to client immediately
                .map(savedProfile -> {
                    ProfileDto responseDto = new ProfileDto();
                    BeanUtils.copyProperties(savedProfile, responseDto);
                    return responseDto;
                });
    }
}
