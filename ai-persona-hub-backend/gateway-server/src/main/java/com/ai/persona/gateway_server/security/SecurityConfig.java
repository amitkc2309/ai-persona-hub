package com.ai.persona.gateway_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

//@Configuration
//@EnableWebFluxSecurity
public class SecurityConfig {
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity,
//                                                         ReactiveClientRegistrationRepository clientRegistrationRepository) {
//        return serverHttpSecurity
//                .authorizeExchange(exchange -> exchange
////                        .pathMatchers("/actuator/**").permitAll()
////                        .pathMatchers(
////                                "/*.css", "/*.js", "/favicon.ico", "/static/**", "/static/css/**"
////                                , "/static/js/**", "/manifest.json", "/*.png", "/*.PNG").permitAll()
//                        .anyExchange().authenticated())
//                .exceptionHandling(exceptionHandlingSpec ->
//                        exceptionHandlingSpec
//                                .authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/"))
//                )
//                .oauth2Login(Customizer.withDefaults())
//                .logout(logout -> logout
//                        // logout
//                        .logoutHandler(logoutHandler())
//                        //logout at keycloak as well
//                        .logoutSuccessHandler(keyCloakLogoutHandler(clientRegistrationRepository)))
//                //CSRF not working because keycloak is not accepting CSRF header
//                /*.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))*/
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .build();
//    }
//
//    private ServerLogoutHandler logoutHandler() {
//        return new DelegatingServerLogoutHandler(
//                new WebSessionServerLogoutHandler(),
//                new CsrfServerLogoutHandler(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//        );
//    }
//
//    private ServerLogoutSuccessHandler keyCloakLogoutHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
//        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
//        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
//        return oidcLogoutSuccessHandler;
//    }
}
