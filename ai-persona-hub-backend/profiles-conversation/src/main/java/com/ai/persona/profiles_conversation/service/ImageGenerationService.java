package com.ai.persona.profiles_conversation.service;

import com.ai.persona.profiles_conversation.constants.CommonConstants;
import com.ai.persona.profiles_conversation.entity.Profile;
import com.ai.persona.profiles_conversation.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageGenerationService {

    private final WebClient webClient;
    private final ProfileRepository profileRepository;
    @Value("${application.image-generation}")
    Boolean imageGeneration;

    public Mono<Profile> generateAndSaveImage(Profile profile) {
        if(Boolean.FALSE.equals(imageGeneration)) {
            log.info("Image generation is disabled");
            return Mono.empty();
        }
        log.info("****STABILITY_AI=" + CommonConstants.getStabilityAi());
        String prompt = "Online profile picture of a " + profile.getAge() + " year old," + profile.getEthnicity() + " " +
                profile.getGender() + " named " + profile.getFirstName() + " " + profile.getLastName() +
                " with personality Type " + profile.getMyersBriggsPersonalityType() + " and Bio:" + profile.getBio() +
                ". Photorealistic skin texture and details, individual hairs and pores visible, highly detailed, " +
                "photorealistic, hyperrealistic, subsurface scattering, 4k DSLR, ultrarealistic, best quality, masterpiece";
        log.info("*******prompt for generating image " + prompt);
        String negativePrompt = "multiple faces, lowres, text, error, cropped, worst quality, low quality, " +
                "jpeg artifacts, ugly,flat nose, duplicate, morbid, mutilated, out of frame, extra fingers, mutated hands, " +
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
                """, prompt, negativePrompt, CommonConstants.getStabilityQuality());
        return webClient.post()
                .uri(CommonConstants.getStabilityAi())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(jsonString))
                .retrieve()
                .bodyToMono(ProfileService.ImageResponse.class)
                .flatMap(imageResponse -> {
                    if (imageResponse != null && imageResponse.images() != null && !imageResponse.images().isEmpty()) {
                        String base64Image = imageResponse.images().get(0);
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                        log.info("****IMAGE_DIR=" + CommonConstants.getImageDir());
                        String directoryPath = CommonConstants.getImageDir();
                        String filePath = directoryPath + "/" + profile.getId() + ".png";
                        Path directory = Paths.get(directoryPath);
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
                        profile.setImageUrls(filePath);
                        return profileRepository.save(profile);
                    }
                    return Mono.empty();
                });
    }
}
