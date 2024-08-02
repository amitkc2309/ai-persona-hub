package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.constants.Gender;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.dto.ProfileMatching;
import com.ai.persona.profiles_conversation.dto.RandomProfileInputDto;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Log
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProfilesController {

    private final ProfileService profileService;

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
    public Mono<ResponseEntity<ProfileDto>> getRandomSavedProfileByGender(@RequestParam(required = false) String gender) {
        return profileService
                .getRandomSavedProfileByGender(gender)
                .map(saved -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok().body(profileDto);
                });
    }

    @GetMapping("/all-bots")
    public Flux<ResponseEntity<ProfileDto>> getAllBots() {
        return profileService
                .getAllBots()
                .map(saved->{
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok(profileDto);
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
    public ResponseEntity<String> generateRandomBotProfile(@RequestBody RandomProfileInputDto randomProfileInputDto) {
        if (randomProfileInputDto == null) {
            return ResponseEntity.badRequest().body("Invalid input");
        }
        Integer age = randomProfileInputDto.getAge();
        String ethnicity = randomProfileInputDto.getEthnicity();
        Gender gender = randomProfileInputDto.getGender();
        String response = profileService.generateRandomBotProfile(gender,age,ethnicity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
