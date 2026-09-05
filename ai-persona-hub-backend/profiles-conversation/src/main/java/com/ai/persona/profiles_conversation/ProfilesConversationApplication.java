package com.ai.persona.profiles_conversation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProfilesConversationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfilesConversationApplication.class, args);
	}

}
