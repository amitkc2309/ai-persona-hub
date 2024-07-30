package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.constants.CommonConstants;
import com.ai.persona.profiles_conversation.constants.Gender;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final OllamaChatModel ollamaChatModel;

    private final WebClient webClient;

    record ImageResponse(List<String> images) {
    }

    public Mono<Profile> getProfile(String profileId) {
        return profileRepository
                .findById(profileId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile", profileId)));
    }

    public Mono<Profile> getProfileByEmail(String email) {
        return profileRepository
                .findByEmail(email)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("email", email)));
    }

    public Mono<Profile> getRandomSavedProfileByGender(String gender) {
        return profileRepository
                .getRandomProfileByGender(gender)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Profile", gender)));
    }

    public Mono<Profile> saveProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(profileDto, profile, "id");
        profile.setMatchedProfiles(new HashSet<>());
        return profileRepository.save(profile);
    }

    public String generateRandomBotProfile(Gender gender,Integer age, String ethnicity) {
        int randomAge = age!=null?age.intValue():CommonUtility.getRandomAge();
        Gender randomGender = gender!=null?gender:CommonUtility.getRandomGender();
        String personalityType = CommonUtility.getPersonalityTypes();
        String randomEthnicity = ethnicity!=null?ethnicity:CommonUtility.getRandomEthnicity();
        String prompt = "Create a Tinder profile persona of an personality Type " + personalityType +
                " " + +randomAge + " year old " + randomEthnicity + " " + randomGender.toString() + " "
                + " including the first name, last name, email, myersBriggsPersonalityType and bio. " +
                "Save the generated profile by calling saveGeneratedProfile function";
        log.info("prompt to create profile: " + prompt);
        CompletableFuture.supplyAsync(()->{
            UserMessage userMessage = new UserMessage(prompt);
            ChatResponse response = ollamaChatModel.call(new Prompt(userMessage,
                    OllamaOptions.builder().withFunction("saveGeneratedProfile").build()));
            log.info("response from ollama:"+response);
            return response;
        });
        return "Profile generation is in progress";
    }

    public Mono<Void> generateAndSaveImage(Profile profile) {
        log.info("****STABILITY_AI="+ CommonConstants.getStabilityAi());
        log.info("****STABILITY_AI_QUALITY="+CommonConstants.getStabilityQuality());
        log.info("*******generating image for " + profile.toString());
        String prompt = profile.getAge() + " years old, " + profile.getMyersBriggsPersonalityType() + " personality "
                + profile.getEthnicity() + " " + profile.getGender() + " photo for tinder bio";
        log.info("*******prompt for generating image " + prompt);
        String negativePrompt = "multiple faces, lowres, text, error, cropped, worst quality, low quality, " +
                "jpeg artifacts, ugly, duplicate, morbid, mutilated, out of frame, extra fingers, mutated hands, " +
                "poorly drawn hands, poorly drawn face, mutation, deformed, blurry, dehydrated, bad anatomy, " +
                "bad proportions, extra limbs, cloned face, disfigured, gross proportions, malformed limbs, " +
                "missing arms, missing legs, extra arms, extra legs, fused fingers, too many fingers, long neck, " +
                "username, watermark, signature";
        String jsonString = String.format("""
                {
                  "prompt": "%s",
                  "negative_prompt": "%s",
                  "steps": "%s"
                }
                """, prompt, negativePrompt,CommonConstants.getStabilityQuality());
        webClient.post()
                .uri(CommonConstants.getStabilityAi())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonString))
                .retrieve()
                .bodyToMono(ImageResponse.class)
                .flatMap(imageResponse -> {
                    if (imageResponse != null && imageResponse.images() != null && !imageResponse.images().isEmpty()) {
                        String base64Image = imageResponse.images().get(0);
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                        log.info("****IMAGE_DIR="+CommonConstants.getImageDir());
                        String directoryPath = CommonConstants.getImageDir();
                        String filePath = directoryPath + "/" + profile.getId() + ".png";
                        Path directory = Paths.get(directoryPath);

                        return Mono.fromCallable(() -> {
                            if (!Files.exists(directory)) {
                                try {
                                    Files.createDirectories(directory);
                                } catch (IOException e) {
                                    log.info("***error creating directory for" + profile.getId());
                                }
                            }
                            try (FileOutputStream imageOutFile = new FileOutputStream(filePath)) {
                                imageOutFile.write(imageBytes);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return filePath;
                        }).subscribeOn(Schedulers.boundedElastic());
                    } else {
                        log.info("No images found in the response.");
                        return Mono.empty();
                    }
                })
                .subscribe(filePath -> {
                            profile.setImageUrls(filePath);
                            profileRepository.save(profile).subscribe();
                            log.info("Image saved at: " + filePath);
                        }
                );
        return Mono.empty();
    }

    public Mono<Profile> updateProfile(ProfileDto profileDto) {
        return profileRepository
                .findById(profileDto.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile", profileDto.getId())))
                .flatMap(saved -> {
                    BeanUtils.copyProperties(profileDto, saved, "id", "myersBriggsPersonalityType", "matchedProfiles");
                    return profileRepository.save(saved);
                });
    }

    public Mono<Void> deleteProfile(String profileId) {
        return profileRepository
                .findById(profileId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile", profileId)))
                .flatMap(saved -> profileRepository.deleteById(profileId));
    }

    /**
     * Add profileId1 match with profile2
     *
     * @param profileId1
     * @param profileId2
     * @return
     */

    public Mono<Profile> addMatchedProfile(String profileId1, String profileId2) {
        return profileRepository
                .findById(profileId1)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile1", profileId1)))
                .flatMap(profile1 ->
                        profileRepository
                                .findById(profileId2)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile2", profileId2)))
                                .flatMap(profile2 -> {
                                    if (profile1.getMatchedProfiles() == null)
                                        profile1.setMatchedProfiles(new HashSet<>());
                                    if (profile2.getMatchedProfiles() == null)
                                        profile2.setMatchedProfiles(new HashSet<>());
                                    profile1.getMatchedProfiles().add(profileId2);
                                    profile2.getMatchedProfiles().add(profileId1);
                                    return profileRepository
                                            .saveAll(List.of(profile1, profile2))
                                            .then(Mono.just(profile1));
                                })
                );
    }
}
