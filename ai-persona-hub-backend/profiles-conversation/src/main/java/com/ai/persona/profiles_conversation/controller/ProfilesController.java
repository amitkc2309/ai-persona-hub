package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.constants.Gender;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.dto.ProfileMatching;
import com.ai.persona.profiles_conversation.service.ProfileService;
import com.ai.persona.profiles_conversation.utils.CommonUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Log
public class ProfilesController {

    private final ProfileService profileService;
    private final OllamaChatModel ollamaChatModel;

    @GetMapping("/{profileId}")
    public Mono<ResponseEntity<ProfileDto>> getProfile(@PathVariable String profileId) {
        return profileService
                .getProfile(profileId)
                .map(saved -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok().body(profileDto);
                });
    }

    @GetMapping("/by-email")
    public Mono<ResponseEntity<ProfileDto>> getProfileByEmail(@RequestParam String email) {
        return profileService
                .getProfileByEmail(email)
                .map(saved -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok().body(profileDto);
                });
    }

    @GetMapping("/random")
    public Mono<ResponseEntity<ProfileDto>> getRandomSavedProfileByGender(@RequestParam String gender) {
        return profileService
                .getRandomSavedProfileByGender(gender)
                .map(saved -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok().body(profileDto);
                });
    }

    @GetMapping("/image/{profileId}")
    public Mono<ResponseEntity<Resource>> getSavedImage(@PathVariable String profileId) {
        return profileService
                .getProfile(profileId)
                .flatMap(profile -> {
                    return Mono.fromSupplier(() -> {
                        Path path = Paths.get(profile.getImageUrls());
                        Resource resource;
                        try {
                            resource = new UrlResource(path.toUri());
                            if (resource.exists() && resource.isReadable()) {
                                return ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + profileId + ".png\"")
                                        .contentType(MediaType.IMAGE_PNG)
                                        .body(resource);
                            } else {
                                return ResponseEntity.notFound().build();
                            }
                        } catch (MalformedURLException e) {
                            return ResponseEntity.badRequest().build();
                        }
                    });
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ProfileDto>> saveProfile(@RequestBody ProfileDto profileDto) {
        return profileService
                .saveProfile(profileDto)
                .map(saved -> {
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.status(HttpStatus.CREATED).body(profileDto);
                });
    }

    @PostMapping("/generate-random")
    public Mono<ResponseEntity<String>> generateRandomBotProfile(@RequestParam Gender gender) {
        int randomAge = CommonUtility.getRandomAge();
        String personalityType = CommonUtility.getPersonalityTypes();
        String randomEthnicity = CommonUtility.getRandomEthnicity();
        String prompt = "Create a Tinder profile persona of an personality Type " + personalityType +
                " " + +randomAge + " year old " + randomEthnicity + " " + gender.toString() + " "
                + " including the first name, last name, email, myersBriggsPersonalityType and bio. " +
                "Save the generated profile by calling saveGeneratedProfile function";
        log.info("prompt to create profile: " + prompt);
        UserMessage userMessage = new UserMessage(prompt);
        ChatResponse response = ollamaChatModel.call(new Prompt(userMessage,
                OllamaOptions.builder().withFunction("saveGeneratedProfile").build()));
        return Mono.just(ResponseEntity.ok().body(response.getResult().getOutput().getContent()));
    }

    @PutMapping
    public Mono<ResponseEntity<ProfileDto>> updateProfile(@RequestBody ProfileDto profileDto) {
        return profileService
                .updateProfile(profileDto)
                .map(saved -> {
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(profileDto);
                });
    }

    @PutMapping("/match")
    public Mono<ResponseEntity<Void>> addMatchedProfile(@RequestBody ProfileMatching profileMatching) {
        return profileService
                .addMatchedProfile(profileMatching.getProfileId1(), profileMatching.getProfileId2())
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @DeleteMapping(("/{profileId}"))
    public Mono<Void> deleteProfile(@PathVariable String profileId) {
        return profileService.deleteProfile(profileId);
    }
}
