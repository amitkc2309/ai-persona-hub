package com.ai.persona.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
public class GatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication gatewayApp = new SpringApplication(GatewayServerApplication.class);
		gatewayApp.setAdditionalProfiles("routes");
		gatewayApp.run(args);
	}

}
