package com.ai.persona.profiles_conversation.integration_test;

import com.ai.persona.profiles_conversation.TestSecurityConfig;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.service.ProfileService;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
        properties = "spring.profiles.active=test", // Will read application-test.yaml
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Must for Controller Test
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class) // Override security config for test
public class ProfileServiceIntegrationTest {


    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        profileRepository.deleteAll().block();
    }

    @Test
    void testSaveAndRetrieveProfile() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId("test-id");
        profileDto.setEmail("test@example.com");
        profileDto.setUsername("testUser");

        //save test
        StepVerifier.create(profileService.saveProfile(profileDto))
                .assertNext(saved->{
                    assertThat(saved.getId()).isNotEmpty();
                    assertThat(saved.getEmail()).isEqualTo("test@example.com");
                    assertThat(saved.getUsername()).isEqualTo("testUser");
                })
                .verifyComplete();

        //retrieve test
        StepVerifier.create(profileService.getProfileByUsername("testUser"))
                .assertNext(found->{
                    assertThat(found.getEmail()).isEqualTo("test@example.com");
                })
                .verifyComplete();
    }

    @Test
    @WithMockUser("testUser")
    void testMatchedProfile() {
        assertThat(SecurityUtils.getUsername()).isEqualTo("testUser");
        ProfileDto profile1 = new ProfileDto();
        profile1.setUsername("testUser");
        profile1.setFirstName("firstName");
        profile1.setLastName("lastName");
        profile1.setEmail("testUser@example.com");
        Profile savedProfile1 = profileService.saveProfile(profile1).block();

        ProfileDto profile2 = new ProfileDto();
        profile2.setUsername("matchedUser");
        profile2.setEmail("matchedUser@example.com");
        Profile savedProfile2 = profileService.saveProfile(profile2).block();

        // Ensure profiles are saved correctly
        assertThat(savedProfile1).isNotNull();
        assertThat(savedProfile2).isNotNull();

        // Test the match logic
        StepVerifier.create(profileService.addMatchedProfileToUser(savedProfile2.getId()))
                .assertNext(loggedUser->{
                    assertThat(loggedUser.getMatchedProfiles().contains(savedProfile2.getId()));
                    assertThat(savedProfile2.getMatchedProfiles().contains(loggedUser.getId()));
                })
                .verifyComplete();

    }

}
