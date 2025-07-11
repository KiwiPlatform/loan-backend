package com.kiwipay.kiwipay_loan_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Basic security configuration for the application.
 * Implements basic security practices for API protection.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(10) // Execute after Swagger security configurations (which have @Order 1, 2, 3)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF disabled for REST API
            .csrf(csrf -> csrf.disable())
            
            // CORS configuration
            .cors(cors -> {})
            
            // Stateless session for REST API
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Enhanced security headers
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(contentType -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            
            // Authorization - All endpoints are public for now
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().permitAll()
            );
            
        return http.build();
    }
} 