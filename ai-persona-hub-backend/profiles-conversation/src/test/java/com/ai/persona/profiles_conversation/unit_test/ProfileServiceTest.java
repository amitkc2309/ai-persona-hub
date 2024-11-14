package com.ai.persona.profiles_conversation.unit_test;

import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.exception.ResourceNotFoundException;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void testGetProfile_whenProfileExists_shouldReturnProfile() {
        Profile profile = new Profile();
        String profileId = "123";
        profile.setId(profileId);

        when(profileRepository.findById(profileId)).thenReturn(Mono.just(profile));

        // StepVerifier is used to test Reactive program otherwise we would have to write
        //profileService.getProfile(profileId).block() and we sure don't want that.
        StepVerifier.create(profileService.getProfile(profileId))
                .expectNext(profile)
                .verifyComplete();
    }

    @Test
    void testGetProfile_whenProfileNotExists_shouldThrowException() {
        Profile profile = new Profile();
        String profileId = "123";
        profile.setId(profileId);

        when(profileRepository.findById(profileId)).thenReturn(Mono.empty());

        StepVerifier.create(profileService.getProfile(profileId))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
