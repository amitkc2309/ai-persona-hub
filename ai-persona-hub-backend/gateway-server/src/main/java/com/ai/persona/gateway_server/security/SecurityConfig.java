package com.ai.persona.gateway_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfServerLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http.cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .authorizeExchange(exchange ->
                        exchange.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .pathMatchers( "/*.css", "/*.js", "/favicon.ico", "/static/**", "/static/css/**"
                                        , "/static/js/**", "/manifest.json", "/*.png", "/*.PNG").permitAll()
                                .pathMatchers("/actuator/health/**").permitAll()
                                .anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
