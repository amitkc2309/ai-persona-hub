package com.ai.persona.profiles_conversation.controller;

import com.ai.persona.profiles_conversation.constants.Gender;
import com.ai.persona.profiles_conversation.dto.PaginatedProfiles;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.dto.RandomProfileInputDto;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.service.ProfileService;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Log
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

    @GetMapping("/profile/{userName}")
    public Mono<ResponseEntity<ProfileDto>> getProfileByUserName(@PathVariable String userName) {
        return profileService
                .getProfileByUsername(userName)
                .map(saved -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok().body(profileDto);
                });
    }

    @GetMapping("/random")
    public Mono<ResponseEntity<ProfileDto>> getRandomSavedProfileByGender(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String excludeLastRandomProfileId) {
        String username = SecurityUtils.getUsername();
        return profileService
                .getRandomSavedBotProfileByGender(gender,excludeLastRandomProfileId)
                .flatMap(random -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(random, profileDto);
                    return profileService.getProfileByUsername(username)
                            .map(user -> {
                                if (user.getMatchedProfiles().contains(profileDto.getId()))
                                    profileDto.setIsMatched(Boolean.TRUE);
                                else
                                    profileDto.setIsMatched(Boolean.FALSE);
                                return ResponseEntity.ok().body(profileDto);
                            });
                });
    }

    @GetMapping("/all-bots")
    public Flux<ResponseEntity<ProfileDto>> getAllBots() {
        return profileService
                .getAllBots()
                .map(saved -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDto);
                    return ResponseEntity.ok(profileDto);
                });
    }

    @GetMapping("/matched-bots")
    public Mono<ResponseEntity<PaginatedProfiles>> getAllMatchedBots(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size
    ) {
        return profileService
                .getAllMatchedBots(page, size)
                .map(PaginatedProfiles->ResponseEntity.ok().body(PaginatedProfiles));
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

    @GetMapping("/user")
    public Mono<ResponseEntity<ProfileDto>> getOrCreateProfile() {
        String username = SecurityUtils.getUsername();
        ProfileDto profileDto = new ProfileDto();
        profileDto.setUsername(username);
        profileDto.setMatchedProfiles(new HashSet<>());
        profileDto.setIsBot(Boolean.FALSE);
        profileDto.setEmail(SecurityUtils.getClaimAsString("email"));
        profileDto.setFirstName(SecurityUtils.getClaimAsString("given_name"));
        profileDto.setLastName(SecurityUtils.getClaimAsString("family_name"));
        return profileService
                .getProfileByUsername(username)
                .map(saved -> {
                    ProfileDto profileDtoExisting = new ProfileDto();
                    BeanUtils.copyProperties(saved, profileDtoExisting);
                    return ResponseEntity.ok().body(profileDtoExisting);
                })
                .switchIfEmpty(
                        profileService
                                .saveProfile(profileDto)
                                .map(savedNew -> {
                                    BeanUtils.copyProperties(savedNew, profileDto);
                                    return ResponseEntity.ok().body(profileDto);
                                })
                );
    }

    @PostMapping("/generate-random")
    public ResponseEntity<String> generateRandomBotProfile(@RequestBody RandomProfileInputDto randomProfileInputDto) {
        if (randomProfileInputDto == null) {
            return ResponseEntity.badRequest().body("Invalid input");
        }
        Integer age = randomProfileInputDto.getAge();
        String ethnicity = randomProfileInputDto.getEthnicity();
        Gender gender = randomProfileInputDto.getGender();
        String response = profileService.generateRandomBotProfile(gender, age, ethnicity);
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

    @PutMapping("/match/{matchedId}")
    public Mono<ResponseEntity<ProfileDto>> addMatchedProfile(@PathVariable String matchedId) {
        return profileService
                .addMatchedProfileToUser(matchedId)
                .map(updatedUser -> {
                    ProfileDto profileDto = new ProfileDto();
                    BeanUtils.copyProperties(updatedUser, profileDto);
                    return ResponseEntity.ok(profileDto);
                });
    }

    @DeleteMapping(("/{profileId}"))
    public Mono<Void> deleteProfile(@PathVariable String profileId) {
        return profileService.deleteProfile(profileId);
    }
}
