package com.ai.persona.gateway_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfServerLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http,
                                              ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return http.cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .authorizeExchange(exchange ->
                        exchange.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .pathMatchers("/*.css", "/*.js", "/favicon.ico", "/static/**", "/static/css/**"
                                        , "/static/js/**", "/manifest.json", "/*.png", "/*.PNG", "/*.ico").permitAll()
                                .pathMatchers("/actuator/health/**").permitAll()
                                .anyExchange().authenticated())
//                .exceptionHandling(exceptionHandlingSpec ->
//                        exceptionHandlingSpec
//                                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/"))
//                )
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
//                        .logoutHandler(logoutHandler())
                        .logoutSuccessHandler(keyCloakLogoutHandler(clientRegistrationRepository)))
                //.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .build();
    }


    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    private ServerLogoutHandler logoutHandler() {
        return new DelegatingServerLogoutHandler(
                new WebSessionServerLogoutHandler(),
                new CsrfServerLogoutHandler(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
        );
    }

    private ServerLogoutSuccessHandler keyCloakLogoutHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }
}
