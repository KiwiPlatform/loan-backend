package com.kiwipay.kiwipay_loan_backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuración de seguridad para Swagger UI
 * Implementa controles de acceso según el entorno
 */
@Configuration
public class SwaggerUISecurityConfig {

    /**
     * Configuración de Swagger UI para desarrollo
     * Acceso completo con todas las funcionalidades
     */
    @Bean
    @Profile("dev")
    @ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
    public GroupedOpenApi developmentApi() {
        return GroupedOpenApi.builder()
                .group("development")
                .displayName("Development API (Full Access)")
                .pathsToMatch("/api/v1/**", "/api/v1/dev/**")
                .build();
    }

    /**
     * API pública para desarrollo
     * Solo endpoints seguros y de consulta
     */
    @Bean
    @Profile("dev")
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .displayName("Public API")
                .pathsToMatch("/api/v1/leads", "/api/v1/clinics", "/api/v1/medical-specialties")
                .pathsToExclude("/api/v1/leads/all/**", "/api/v1/populate/**")
                .build();
    }

    /**
     * API administrativa para desarrollo
     * Solo endpoints críticos y administrativos
     */
    @Bean
    @Profile("dev")
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("Admin API (Restricted)")
                .pathsToMatch("/api/v1/leads/all/**", "/api/v1/populate/**", "/actuator/**")
                .build();
    }

    /**
     * Configuración de Swagger UI para staging
     * Acceso limitado solo a endpoints seguros
     */
    @Bean
    @Profile("staging")
    @ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = false)
    public GroupedOpenApi stagingApi() {
        return GroupedOpenApi.builder()
                .group("staging")
                .displayName("Staging API (Limited Access)")
                .pathsToMatch("/api/v1/leads", "/api/v1/clinics", "/api/v1/medical-specialties")
                .pathsToExclude("/api/v1/leads/all/**", "/api/v1/populate/**", "/actuator/**")
                .build();
    }

    /**
     * Configuración de Swagger UI para producción
     * DESHABILITADO por seguridad - solo documentación estática
     */
    @Bean
    @Profile("prod")
    @ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "false")
    public GroupedOpenApi productionApi() {
        // En producción, Swagger UI está completamente deshabilitado
        // Solo se permite acceso a documentación estática
        return GroupedOpenApi.builder()
                .group("production-docs")
                .displayName("Production Documentation (Static Only)")
                .pathsToMatch("/docs/**") // Solo documentación estática
                .build();
    }
} 