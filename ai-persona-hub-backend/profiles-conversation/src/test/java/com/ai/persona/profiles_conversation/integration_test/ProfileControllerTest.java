package com.ai.persona.profiles_conversation.integration_test;

import com.ai.persona.profiles_conversation.TestSecurityConfig;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.service.ProfileService;
import com.ai.persona.profiles_conversation.utils.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = "spring.profiles.active=test", // Will read application-test.yaml
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //Must for Controller Test
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class) // Override security config for test
public class ProfileControllerTest {
    @Autowired
    private WebTestClient webTestClient; // For testing the controller layer

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileService profileService;

    @AfterEach
    void deleteTestProfile() {
        profileRepository.deleteAll().block();
    }

    @Test
    void testGetProfileThroughController() {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId("controller-id");
        profileDto.setEmail("controller@example.com");
        profileDto.setUsername("controllerUser");

        // Save profile directly to repository
        profileService.saveProfile(profileDto).block();

        // Test the endpoint
        webTestClient.get()
                .uri("/profiles/profile/{userName}", "controllerUser")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("controllerUser")
                .jsonPath("$.email").isEqualTo("controller@example.com");
    }

    @Test
    void testSaveProfileViaController(){
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId("controller-id");
        profileDto.setEmail("controller@example.com");
        profileDto.setFirstName("firstName");
        profileDto.setLastName("lastName");
        profileDto.setUsername("controllerUser");

        AtomicReference<String> createdProfileId = new AtomicReference<>();
        //Save Test
        webTestClient
                .post()
                .uri("/profiles")
                .bodyValue(profileDto)
                .exchange()
                .expectStatus().isCreated() //check for 201
                .expectBody(ProfileDto.class)
                .value(saved->{
                    createdProfileId.set(saved.getId()); // Store ID for deletion
                    assertThat(saved.getId()).isNotEmpty();
                    assertThat(saved.getEmail()).isEqualTo("controller@example.com");
                    assertThat(saved.getUsername()).isEqualTo("controllerUser");
                    assertThat(saved.getFirstName()).isEqualTo("firstName");
                    assertThat(saved.getLastName()).isEqualTo("lastName");
                });

        //delete test
        webTestClient
                .delete()
                .uri("/profiles/{profileId}",createdProfileId.get())
                .exchange()
                .expectStatus().isOk();

    }

    //@Test
    @WithMockUser("testUser")
    void testMatchedProfile() {
            assertThat(SecurityUtils.getUsername()).isEqualTo("testUser");
        // Create Profile 1 (the user that will match the profile)
            ProfileDto profile1 = new ProfileDto();
            profile1.setUsername("testUser");
            profile1.setFirstName("firstName");
            profile1.setLastName("lastName");
            profile1.setEmail("testUser@example.com");
            Profile savedProfile1 = profileService.saveProfile(profile1).block();

            // Create Profile 2 (the profile to be matched)
            ProfileDto profile2 = new ProfileDto();
            profile2.setUsername("matchedUser");
            profile2.setEmail("matchedUser@example.com");
            Profile savedProfile2 = profileService.saveProfile(profile2).block();

            // Ensure profiles are saved correctly
            assertThat(savedProfile1).isNotNull();
            assertThat(savedProfile2).isNotNull();

            // Test the match logic

            webTestClient.put()
                    .uri("/profiles/match/{matchedId}",savedProfile2.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(ProfileDto.class)
                    .value(responseProfile -> {
                        assertThat(responseProfile.getUsername()).isEqualTo("testUser");
                        assertThat(responseProfile.getMatchedProfiles()).contains(savedProfile2.getId());
                    });
        }

        @Test
        void securityContextAuthTest() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getName()).thenReturn("testUser"); // Return the username

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("testUser");
    }

    @Test
    void getCurrentDummyLoggedUserTest() {
        try(var mocked=mockStatic(SecurityUtils.class)){ //This is how you mock static methods
            mocked.when(SecurityUtils::getUsername).thenReturn("testUser");
            assertThat(SecurityUtils.getUsername()).isEqualTo("testUser");
        }
    }
}


