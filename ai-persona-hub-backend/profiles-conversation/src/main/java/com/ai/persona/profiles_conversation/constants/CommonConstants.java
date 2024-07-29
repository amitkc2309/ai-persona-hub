package com.ai.persona.profiles_conversation.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConstants {

    private static String IMAGE_DIR;
    private static String STABILITY_AI;
    private static String STABILITY_QUALITY;

    @Value("${stability.quality}")
    public void setStabilityQuality(String stabilityQuality) {
        STABILITY_QUALITY = stabilityQuality;
    }

    @Value("${image.dir}")
    public void setImageDir(String imageDir) {
        CommonConstants.IMAGE_DIR = imageDir;
    }

    @Value("${stability.ai}")
    public void setStabilityAi(String stabilityAi) {
        CommonConstants.STABILITY_AI = stabilityAi;
    }

    public static String getStabilityQuality() {
        return STABILITY_QUALITY;
    }

    public static String getImageDir() {
        return IMAGE_DIR;
    }

    public static String getStabilityAi() {
        return STABILITY_AI;
    }
}
