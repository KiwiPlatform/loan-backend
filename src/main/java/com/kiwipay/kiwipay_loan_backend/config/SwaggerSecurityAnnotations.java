package com.kiwipay.kiwipay_loan_backend.config;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotaciones personalizadas para documentar seguridad en Swagger
 * Siguiendo mejores prácticas de documentación de APIs empresariales
 */
public class SwaggerSecurityAnnotations {

    /**
     * Requiere autenticación JWT Bearer Token
     * Para endpoints que requieren usuario autenticado
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirement(name = "Bearer Authentication")
    public @interface RequireJwtAuth {
    }

    /**
     * Requiere API Key para integraciones externas
     * Para endpoints de sistemas automatizados
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirement(name = "API Key Authentication")
    public @interface RequireApiKey {
    }

    /**
     * Requiere OAuth2 con scopes específicos
     * Para endpoints con permisos granulares
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirement(name = "OAuth2 Authorization Code")
    public @interface RequireOAuth2 {
    }

    /**
     * Múltiples opciones de autenticación
     * Usuario puede elegir JWT o OAuth2
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements({
        @SecurityRequirement(name = "Bearer Authentication"),
        @SecurityRequirement(name = "OAuth2 Authorization Code")
    })
    public @interface RequireAuthAny {
    }

    /**
     * Requiere autenticación de administrador
     * Para endpoints críticos y administrativos
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements({
        @SecurityRequirement(name = "Bearer Authentication"),
        @SecurityRequirement(name = "OAuth2 Authorization Code")
    })
    public @interface RequireAdminAuth {
    }

    /**
     * Solo para desarrollo - Basic Auth permitido
     * NUNCA usar en producción
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements({
        @SecurityRequirement(name = "Bearer Authentication"),
        @SecurityRequirement(name = "Basic Authentication")
    })
    public @interface DevOnlyAuth {
    }
} 