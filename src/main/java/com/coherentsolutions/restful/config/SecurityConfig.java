package com.coherentsolutions.restful.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // In-memory user details service
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("0oa157tvtugfFXEhU4x7")
                .password(passwordEncoder().encode("X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Security filter chain for the H2 console.
     * This must have the highest precedence to ensure that requests to /h2-console/** are handled correctly.
     */
    @Bean
    @Order(0)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**") // Apply this filter chain only to /h2-console/**
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Permit all requests to H2 console
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for H2 console
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Allow frames from the same origin
                );
        return http.build();
    }

    /**
     * Security filter chain for the OAuth2 Authorization Server.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // Apply default OAuth2 Authorization Server configurations
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Enable OpenID Connect 1.0
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        // Configure form login
        return http.formLogin(Customizer.withDefaults()).build();
    }

    /**
     * Security filter chain for the Resource Server (your API endpoints).
     * This filter chain should only apply to requests under /api/**
     */
    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Apply this filter chain only to /api/**
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated() // All /api/** requests require authentication
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()) // Enable JWT-based authentication
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF protection for APIs

        return http.build();
    }


    /**
     * Registered Client Repository Bean for OAuth2 clients.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("0oa157tvtugfFXEhU4x7")
                .clientSecret(passwordEncoder().encode("X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read")
                .scope("write")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /**
     * Authorization Server Settings Bean.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8080")
                .jwkSetEndpoint("/oauth2/jwks")  // Correct JWK set endpoint
                .build();
    }

}
