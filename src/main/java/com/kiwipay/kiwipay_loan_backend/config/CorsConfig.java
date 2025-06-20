package com.kiwipay.kiwipay_loan_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for the application.
 * Allows frontend applications to communicate with the API.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${kiwipay.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${kiwipay.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${kiwipay.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${kiwipay.cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(List.of(allowedHeaders));
        configuration.setAllowCredentials(allowCredentials);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
} 