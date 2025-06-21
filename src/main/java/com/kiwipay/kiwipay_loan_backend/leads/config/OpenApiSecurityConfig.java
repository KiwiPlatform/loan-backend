package com.kiwipay.kiwipay_loan_backend.leads.config;

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
 * Configuración de seguridad empresarial para OpenAPI/Swagger
 * Implementa autenticación por variables secretas para entornos no productivos
 * 
 * PERFILES DE SEGURIDAD:
 * - PRODUCCIÓN (default): Swagger completamente OCULTO
 * - STAGING: Acceso con autenticación por variable secreta SWAGGER_STAGING_SECRET
 * - DESARROLLO: Acceso con autenticación por variable secreta SWAGGER_DEV_SECRET
 * 
 * @author alexander.castillo@kiwipay.pe
 * @version 2.0.0 - Seguridad Empresarial
 */
@Configuration
public class OpenApiSecurityConfig {
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * CONFIGURACIÓN PRODUCCIÓN (DEFAULT)
     * Swagger completamente DESHABILITADO por seguridad empresarial
     * Solo documentación básica sin detalles técnicos
     */
    @Bean
    @Profile("!dev & !staging")
    public OpenAPI productionOpenAPI() {
        return new OpenAPI()
                .info(productionApiInfo())
                .servers(List.of(
                    new Server()
                        .url("https://api.kiwipay.pe" + contextPath)
                        .description("Servidor de Producción Seguro")
                ))
                .components(new Components()) // Sin esquemas de seguridad expuestos
                .extensions(java.util.Map.of(
                    "x-security-level", "MAXIMUM",
                    "x-environment", "PRODUCTION",
                    "x-swagger-access", "DISABLED"
                ));
    }

    /**
     * CONFIGURACIÓN STAGING
     * Acceso global con autenticación por variable secreta
     * Requiere SWAGGER_STAGING_SECRET en variables de entorno
     */
    @Bean
    @Profile("staging")
    public OpenAPI stagingOpenAPI() {
        return new OpenAPI()
                .info(stagingApiInfo())
                .servers(List.of(
                    new Server()
                        .url("https://staging-api.kiwipay.pe" + contextPath)
                        .description("Servidor de Staging - Acceso Restringido"),
                    new Server()
                        .url("http://localhost:8080" + contextPath)
                        .description("Servidor Local de Staging")
                ))
                .components(stagingSecurityComponents())
                .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication")
                    .addList("OAuth2 Authorization Code")
                )
                .extensions(java.util.Map.of(
                    "x-security-level", "HIGH",
                    "x-environment", "STAGING",
                    "x-swagger-access", "RESTRICTED",
                    "x-auth-required", "SWAGGER_STAGING_SECRET"
                ));
    }

    /**
     * CONFIGURACIÓN DESARROLLO
     * Acceso global con autenticación por variable secreta
     * Requiere SWAGGER_DEV_SECRET en variables de entorno
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
                        .url("https://dev-api.kiwipay.pe" + contextPath)
                        .description("Servidor de Desarrollo Remoto")
                ))
                .components(developmentSecurityComponents())
                .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication")
                    .addList("Basic Authentication")
                )
                .extensions(java.util.Map.of(
                    "x-security-level", "MEDIUM",
                    "x-environment", "DEVELOPMENT", 
                    "x-swagger-access", "RESTRICTED",
                    "x-auth-required", "SWAGGER_DEV_SECRET"
                ));
    }

    /**
     * Información de API para PRODUCCIÓN
     * Información mínima sin detalles técnicos sensibles
     */
    private Info productionApiInfo() {
        return new Info()
                .title("KiwiPay Loan API")
                .description("""
                    ## API Empresarial para Gestión de Préstamos Médicos
                    
                    ### ENTORNO DE PRODUCCIÓN
                    - Documentación técnica no disponible por seguridad
                    - Todos los endpoints requieren autenticación válida
                    - Cumplimiento total con estándares de seguridad empresarial
                    
                    ### Contacto Técnico
                    Para soporte técnico o consultas de integración, contactar al desarrollador principal.
                    
                    ### Términos de Uso
                    API de uso exclusivo para sistemas autorizados de KiwiPay.
                    Acceso no autorizado está prohibido y será reportado.
                    """)
                .version(appVersion)
                .contact(new Contact()
                    .name("Desarrollador Principal")
                    .email("alexander.castillo@kiwipay.pe"))
                .license(new License()
                    .name("Proprietary License")
                    .url("https://kiwipay.pe/license"));
    }

    /**
     * Información de API para STAGING
     * Documentación completa con advertencias de seguridad
     */
    private Info stagingApiInfo() {
        return new Info()
                .title("KiwiPay Loan API - STAGING")
                .description("""
                    ## ENTORNO DE STAGING - ACCESO RESTRINGIDO
                    
                    ### ADVERTENCIAS DE SEGURIDAD:
                    - Entorno de pre-producción con datos de prueba
                    - Requiere autenticación con clave secreta mensual
                    - Todos los accesos son registrados y auditados
                    - Sesiones expiran automáticamente en 8 horas
                    
                    ### Características de Seguridad:
                    - Autenticación OAuth2 + JWT obligatoria
                    - Rate limiting aplicado por usuario
                    - Encriptación end-to-end para datos sensibles
                    - Validación exhaustiva de entrada
                    - Headers de seguridad OWASP implementados
                    
                    ### Funcionalidades Disponibles:
                    - Gestión completa de leads médicos
                    - Integración con clínicas y especialidades
                    - Sistema de auditoría completo
                    - Métricas y reportes avanzados
                    
                    ### Autenticación:
                    Para acceder a esta documentación, necesitas la clave secreta mensual de staging.
                    Contacta al desarrollador principal si no tienes acceso.
                    """)
                .version(appVersion + "-STAGING")
                .contact(new Contact()
                    .name("KiwiPay Development Team")
                    .email("alexander.castillo@kiwipay.pe")
                    .url("https://kiwipay.pe/dev"))
                .license(new License()
                    .name("Internal Development License")
                    .url("https://kiwipay.pe/dev-license"));
    }

    /**
     * Información de API para DESARROLLO
     * Documentación completa con herramientas de testing
     */
    private Info developmentApiInfo() {
        return new Info()
                .title("KiwiPay Loan API - DESARROLLO")
                .description("""
                    ## ENTORNO DE DESARROLLO - HERRAMIENTAS COMPLETAS
                    
                    ### Acceso de Desarrollo:
                    - Entorno de desarrollo con datos de prueba
                    - Requiere autenticación con clave secreta mensual
                    - Herramientas de testing y debugging disponibles
                    - Documentación interactiva completa
                    
                    ### Características de Desarrollo:
                    - Autenticación flexible (JWT + Basic Auth)
                    - Rate limiting relajado para testing
                    - Logs de debug activados
                    - CORS permisivo para desarrollo frontend
                    - Swagger UI con todas las funcionalidades
                    
                    ### Herramientas Disponibles:
                    - Testing interactivo de todos los endpoints
                    - Generación automática de datos de prueba
                    - Métricas de rendimiento en tiempo real
                    - Validación de esquemas JSON
                    - Exportación de colecciones Postman
                    
                    ### Endpoints Adicionales de Desarrollo:
                    - GET /api/v1/dev/health - Health check detallado
                    - POST /api/v1/dev/seed - Generar datos de prueba
                    - GET /api/v1/dev/metrics - Métricas de desarrollo
                    - DELETE /api/v1/dev/reset - Limpiar datos de prueba
                    
                    ### Autenticación:
                    Para acceder a esta documentación, necesitas la clave secreta mensual de desarrollo.
                    """)
                .version(appVersion + "-DEV")
                .contact(new Contact()
                    .name("KiwiPay Development Team")
                    .email("alexander.castillo@kiwipay.pe")
                    .url("https://kiwipay.pe/dev"));
    }

    /**
     * Componentes de seguridad para STAGING
     * OAuth2 + JWT con máxima seguridad
     */
    private Components stagingSecurityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", stagingBearerAuthScheme())
                .addSecuritySchemes("OAuth2 Authorization Code", stagingOAuth2Scheme());
    }

    /**
     * Componentes de seguridad para DESARROLLO
     * JWT + Basic Auth para flexibilidad en testing
     */
    private Components developmentSecurityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", developmentBearerAuthScheme())
                .addSecuritySchemes("Basic Authentication", developmentBasicAuthScheme());
    }

    /**
     * JWT Bearer Authentication para STAGING
     */
    private SecurityScheme stagingBearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                    ### JWT Bearer Authentication - STAGING
                    
                    **Formato:** Authorization: Bearer <jwt-token>
                    
                    **Características de Seguridad:**
                    - Token expira en 1 hora (3600 segundos)
                    - Algoritmo: RS256 (RSA + SHA256)
                    - Claims obligatorios: sub, iat, exp, roles, permissions
                    - Refresh token válido por 24 horas
                    - Rotación automática de claves cada 30 días
                    
                    **Ejemplo de Header:**
                    Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
                    
                    **Obtener Token:**
                    - Endpoint: POST /api/v1/auth/login
                    - Requiere credenciales válidas de staging
                    - Retorna access_token y refresh_token
                    
                    **Importante:**
                    - Nunca compartir tokens en logs o URLs
                    - Usar HTTPS exclusivamente
                    - Implementar logout para invalidar tokens
                    """);
    }

    /**
     * JWT Bearer Authentication para DESARROLLO
     */
    private SecurityScheme developmentBearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                    ### JWT Bearer Authentication - DESARROLLO
                    
                    **Formato:** Authorization: Bearer <jwt-token>
                    
                    **Características de Desarrollo:**
                    - Token expira en 8 horas (para desarrollo cómodo)
                    - Algoritmo: HS256 (más simple para desarrollo)
                    - Claims básicos: sub, iat, exp, roles
                    - Refresh token válido por 7 días
                    
                    **Tokens de Desarrollo Predefinidos:**
                    - Admin: dev-admin-token-2025
                    - User: dev-user-token-2025
                    - ReadOnly: dev-readonly-token-2025
                    
                    **Obtener Token:**
                    - Endpoint: POST /api/v1/auth/dev-login
                    - Credenciales simples para desarrollo
                    """);
    }

    /**
     * OAuth2 para STAGING
     */
    private SecurityScheme stagingOAuth2Scheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("""
                    ### OAuth2 Authorization Code Flow - STAGING
                    
                    **Flujo de Autorización Empresarial:**
                    1. Authorization Request con PKCE
                    2. User Authorization (SSO corporativo)
                    3. Authorization Grant con code_challenge
                    4. Access Token Request con code_verifier
                    5. Token Response con refresh_token
                    
                    **Scopes de Staging:**
                    - leads:read - Leer información de leads
                    - leads:write - Crear y actualizar leads
                    - leads:delete - Eliminar leads (solo admin)
                    - clinics:read - Consultar clínicas
                    - clinics:write - Gestionar clínicas (solo admin)
                    - reports:read - Acceso a reportes
                    - admin:all - Acceso administrativo completo
                    
                    **Endpoints OAuth2:**
                    - Authorization: https://auth-staging.kiwipay.pe/oauth2/authorize
                    - Token: https://auth-staging.kiwipay.pe/oauth2/token
                    - Revoke: https://auth-staging.kiwipay.pe/oauth2/revoke
                    - UserInfo: https://auth-staging.kiwipay.pe/oauth2/userinfo
                    """)
                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                    .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                        .authorizationUrl("https://auth-staging.kiwipay.pe/oauth2/authorize")
                        .tokenUrl("https://auth-staging.kiwipay.pe/oauth2/token")
                        .refreshUrl("https://auth-staging.kiwipay.pe/oauth2/refresh")
                        .scopes(new io.swagger.v3.oas.models.security.Scopes()
                            .addString("leads:read", "Leer información de leads")
                            .addString("leads:write", "Crear y actualizar leads")
                            .addString("leads:delete", "Eliminar leads")
                            .addString("clinics:read", "Consultar clínicas")
                            .addString("clinics:write", "Gestionar clínicas")
                            .addString("reports:read", "Acceso a reportes")
                            .addString("admin:all", "Acceso administrativo completo")
                        )
                    )
                );
    }

    /**
     * Basic Authentication para DESARROLLO
     */
    private SecurityScheme developmentBasicAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")
                .description("""
                    ### Basic Authentication - SOLO DESARROLLO
                    
                    **ADVERTENCIA:** Solo habilitado en entorno de desarrollo
                    
                    **Formato:** Authorization: Basic <base64(username:password)>
                    
                    **Credenciales de Desarrollo:**
                    - Admin: dev-admin / dev-admin-2025!
                    - User: dev-user / dev-user-2025!
                    - ReadOnly: dev-readonly / dev-readonly-2025!
                    
                    **Ejemplo:**
                    Username: dev-admin
                    Password: dev-admin-2025!
                    Base64: ZGV2LWFkbWluOmRldi1hZG1pbi0yMDI1IQ==
                    Header: Authorization: Basic ZGV2LWFkbWluOmRldi1hZG1pbi0yMDI1IQ==
                    
                    **NUNCA USAR EN PRODUCCIÓN:**
                    - Credenciales en texto plano
                    - Sin encriptación robusta
                    - Solo para pruebas y desarrollo local
                    """);
    }
} 