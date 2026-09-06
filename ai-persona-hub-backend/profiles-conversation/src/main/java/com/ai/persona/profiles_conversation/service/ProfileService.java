package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.dto.PaginatedProfiles;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class ProfileService {

    private final ProfileRepository profileRepository;

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

    public Mono<Profile> getProfileByUsername(String username) {
        return profileRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.empty());
    }

    public Mono<Profile> getRandomSavedBotProfileByGender(String gender, String excludeLastRandomProfileId) {
        if (gender == null || gender.length() < 2) {
            if (excludeLastRandomProfileId != null && excludeLastRandomProfileId.length() > 1) {
                return profileRepository
                        .getRandomBotProfileExceptLastRandom(excludeLastRandomProfileId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Profile", "-no gender specified")));
            } else
                return profileRepository
                        .getRandomBotProfile()
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Profile", "-no gender specified")));
        } else {
            if (excludeLastRandomProfileId != null && excludeLastRandomProfileId.length() > 1) {
                return profileRepository
                        .getRandomBotProfileByGenderExceptLastRandom(gender, excludeLastRandomProfileId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Profile", gender)));
            } else {
                return profileRepository
                        .getRandomBotProfileByGender(gender)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Profile", gender)));
            }
        }
    }

    public Mono<Profile> saveProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(profileDto, profile, "id");
        profile.setMatchedProfiles(new HashSet<>());
        log.info("*****saving profile:"+profile);
        return profileRepository.save(profile);
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

    public Mono<Profile> addMatchedProfileToUser(String matchedId) {
        String username = SecurityUtils.getUsername();
        return getProfileByUsername(username)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("user", username)))
                .flatMap(SavedUser -> this.addMatchedProfile(SavedUser.getId(), matchedId));

    }

    public Mono<Profile> addMatchedProfile(String profileId1, String profileId2) {
        return profileRepository
                .findById(profileId1)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("user", profileId1)))
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

    public Flux<Profile> getAllBots() {
        return profileRepository
                .findAllBots()
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("profile1", "AI Bots")));
    }

    public Mono<PaginatedProfiles> getAllMatchedBots(int page, int size) {
        String username = SecurityUtils.getUsername();
        return getProfileByUsername(username)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("user", username)))
                .flatMap(savedUser ->
                        {
                            log.info("***********************inside service method getAllMatchedBots ");
                            Set<String> matchedProfiles = savedUser.getMatchedProfiles();
                            int totalCount = matchedProfiles.size();
                            return profileRepository
                                    .findAllByIdWithPagination(matchedProfiles, page, size)
                                    .collectList()
                                    .map(profiles -> {
                                        List<ProfileDto> profileDtos = profiles.stream()
                                                .map(profile -> {
                                                    ProfileDto profileDto = new ProfileDto();
                                                    BeanUtils.copyProperties(profile, profileDto);
                                                    return profileDto;
                                                })
                                                .collect(Collectors.toList());
                                        PaginatedProfiles paginatedProfiles = new PaginatedProfiles();
                                        paginatedProfiles.setProfiles(profileDtos);
                                        paginatedProfiles.setPageSize(size);
                                        paginatedProfiles.setTotalCount(totalCount);
                                        paginatedProfiles.setCurrentPage(page);
                                        paginatedProfiles.setTotalPages(calculateTotalPages(totalCount,size));
                                        return paginatedProfiles;
                                    });
                        }
                );
    }

    private int calculateTotalPages(int totalItems, int size) {
        return (int) Math.ceil((double) totalItems / size);
    }
}
