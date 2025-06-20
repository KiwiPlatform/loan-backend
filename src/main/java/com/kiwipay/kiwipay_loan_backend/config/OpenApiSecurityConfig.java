package com.kiwipay.kiwipay_loan_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Configuración de seguridad avanzada para OpenAPI/Swagger
 * Implementa mejores prácticas empresariales según OWASP y OpenAPI Security Specification
 */
@Configuration
public class OpenApiSecurityConfig {
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @Value("${app.environment:development}")
    private String environment;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * Configuración específica para desarrollo
     * Incluye endpoints adicionales y relajación de seguridad
     */
    @Bean
    @Profile("dev")
    public OpenAPI developmentOpenAPI() {
        return new OpenAPI()
                .info(developmentApiInfo())
                .servers(List.of(
                    new Server()
                        .url("http://localhost:8080" + contextPath)
                        .description("Servidor de Desarrollo Local"),
                    new Server()
                        .url("https://staging-api.kiwipay.pe" + contextPath)
                        .description("Servidor de Staging (HTTPS)")
                ))
                .components(developmentSecurityComponents())
                .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication")
                    .addList("Basic Authentication")
                );
    }

    /**
     * Configuración específica para producción
     * Máxima seguridad y mínima exposición
     */
    @Bean
    @Profile("prod")
    public OpenAPI productionOpenAPI() {
        return new OpenAPI()
                .info(productionApiInfo())
                .servers(List.of(
                    new Server()
                        .url("https://api.kiwipay.pe" + contextPath)
                        .description("Servidor de Producción (HTTPS)")
                ))
                .components(productionSecurityComponents())
                .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication")
                    .addList("OAuth2 Authorization Code")
                );
    }

    /**
     * Configuración por defecto (para otros perfiles)
     */
    @Bean
    @Profile("!dev & !prod")
    public OpenAPI defaultOpenAPI() {
        return new OpenAPI()
                .info(defaultApiInfo())
                .servers(List.of(
                    new Server()
                        .url("http://localhost:8080" + contextPath)
                        .description("Servidor Local")
                ))
                .components(defaultSecurityComponents())
                .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication")
                );
    }

    /**
     * Información de API para desarrollo
     */
    private Info developmentApiInfo() {
        return new Info()
                .title("KiwiPay Loan Backend API - DESARROLLO")
                .description("""
                    ## API para Gestión de Préstamos Médicos - MODO DESARROLLO
                    
                    ### Características de desarrollo:
                    - Basic Auth habilitado para pruebas
                    - Endpoints de testing disponibles
                    - Logs de debug activados
                    - CORS permisivo
                    - HTTPS opcional
                    
                    ### Autenticación disponible:
                    - JWT Bearer Token (recomendado)
                    - Basic Authentication (solo desarrollo)
                    
                    ### Endpoints adicionales:
                    - Health checks detallados
                    - Métricas de desarrollo
                    """)
                .version(appVersion)
                .contact(new Contact()
                    .name("KiwiPay Development Team")
                    .email("alexander.castillo@kiwipay.pe")
                    .url("https://kiwipay.pe/dev"));
    }

    /**
     * Información de API para producción
     */
    private Info productionApiInfo() {
        return new Info()
                .title("KiwiPay Loan Backend API")
                .description("""
                    ## API PRODUCCIÓN - MÁXIMA SEGURIDAD
                    
                    ### ADVERTENCIAS IMPORTANTES:
                    - HTTPS OBLIGATORIO
                    - Autenticación requerida en TODOS los endpoints
                    - Rate limiting estricto aplicado
                    - Logs de auditoría completos
                    
                    ### Características de seguridad:
                    - OAuth2 + JWT únicamente
                    - Encriptación para datos sensibles
                    - Headers de seguridad OWASP
                    - Validación exhaustiva
                    
                    ### Cumplimiento:
                    - OWASP API Security Top 10
                    - ISO 27001 Guidelines
                    - GDPR Compliance
                    """)
                .version(appVersion)
                .contact(new Contact()
                    .name("KiwiPay Security Team")
                    .email("alexander.castillo@kiwipay.pe")
                    .url("https://kiwipay.pe/security"))
                .license(new License()
                    .name("Proprietary License")
                    .url("https://kiwipay.pe/license"));
    }

    /**
     * Información de API por defecto
     */
    private Info defaultApiInfo() {
        return new Info()
                .title("KiwiPay Loan Backend API")
                .description("API para Gestión de Préstamos Médicos")
                .version(appVersion)
                .contact(new Contact()
                    .name("KiwiPay Team")
                    .email("support@kiwipay.pe"));
    }

    /**
     * Componentes de seguridad para desarrollo
     */
    private Components developmentSecurityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", bearerAuthScheme())
                .addSecuritySchemes("Basic Authentication", basicAuthScheme());
    }

    /**
     * Componentes de seguridad para producción
     */
    private Components productionSecurityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", bearerAuthScheme())
                .addSecuritySchemes("OAuth2 Authorization Code", oauth2Scheme());
    }

    /**
     * Componentes de seguridad por defecto
     */
    private Components defaultSecurityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", bearerAuthScheme());
    }

    /**
     * JWT Bearer Token Authentication
     */
    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                    ### JWT Bearer Authentication
                    
                    **Formato:** `Authorization: Bearer <jwt-token>`
                    
                    **Características:**
                    - Token expira en 1 hora
                    - Algoritmo: RS256
                    - Claims: sub, iat, exp, roles
                    
                    **Ejemplo:**
                    ```
                    Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
                    ```
                    """);
    }

    /**
     * Basic Authentication (Solo para desarrollo)
     */
    private SecurityScheme basicAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .description("""
                    ### Basic Authentication (SOLO DESARROLLO)
                    
                    **ADVERTENCIA:** Solo habilitado en entorno de desarrollo
                    
                    **Formato:** Authorization: Basic <base64(username:password)>
                    
                    **NUNCA usar en producción**
                    """);
    }

    /**
     * OAuth2 Authorization Code Flow
     */
    private SecurityScheme oauth2Scheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("""
                    ### OAuth2 Authorization Code Flow
                    
                    **Flujo completo OAuth2:**
                    1. Authorization Request
                    2. User Authorization
                    3. Access Token Request
                    
                    **Scopes disponibles:**
                    - leads:read - Leer leads
                    - leads:write - Crear/actualizar leads
                    - admin:all - Acceso administrativo
                    """)
                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                    .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                        .authorizationUrl("https://auth.kiwipay.pe/oauth2/authorize")
                        .tokenUrl("https://auth.kiwipay.pe/oauth2/token")
                        .scopes(new io.swagger.v3.oas.models.security.Scopes()
                            .addString("leads:read", "Leer información de leads")
                            .addString("leads:write", "Crear y actualizar leads")
                            .addString("admin:all", "Acceso administrativo completo")
                        )
                    )
                );
    }
} 