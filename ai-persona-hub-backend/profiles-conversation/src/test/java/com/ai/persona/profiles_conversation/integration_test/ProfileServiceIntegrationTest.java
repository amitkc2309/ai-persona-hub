package com.ai.persona.profiles_conversation.integration_test;

import com.ai.persona.profiles_conversation.TestSecurityConfig;
import com.ai.persona.profiles_conversation.dto.ProfileDto;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import com.ai.persona.profiles_conversation.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
    private WebTestClient webTestClient; // For testing the controller layer

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
}
