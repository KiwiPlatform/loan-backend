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

import java.util.List;

/**
 * Configuración simplificada de Swagger con Bearer Token JWT
 * 
 * @author alexander.castillo@kiwipay.pe
 * @version 2.1.0 - Configuración Simplificada
 */
@Configuration
public class OpenApiSecurityConfig {
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * Configuración única y simplificada de OpenAPI
     * Solo Bearer Token JWT para todos los entornos
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                    new Server()
                        .url("http://localhost:8080" + contextPath)
                        .description("Servidor Local"),
                    new Server()
                        .url("https://loan-backend-develop.onrender.com" + contextPath)
                        .description("Servidor de Desarrollo")
                ))
                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement()
                    .addList("Bearer Authentication")
                );
    }

    /**
     * Información básica de la API
     */
    private Info apiInfo() {
        return new Info()
                .title("KiwiPay  Platform")
                .description("""
                    ## Platform para Gestión de Préstamos Médicos
                    
                    ### Autenticación:
                    Esta Platform utiliza **Bearer Token (JWT)** para la autenticación.
                    
                    ### Pasos para autenticarte:
                    1. **Registrar usuario** (si no tienes cuenta): POST `/api/v1/auth/register`
                    2. **Hacer login**: POST `/api/v1/auth/login`
                    3. **Copiar el token** de la respuesta
                    4. **Hacer clic en 'Authorize'** (🔒 botón en la parte superior)
                    5. **Pegar el token** en el campo "Value" (sin escribir "Bearer")
                    6. **Clic en "Authorize"** y luego "Close"
                    
                    ¡Ya puedes usar todos los endpoints protegidos!
                    
                    ### Endpoints disponibles:
                    - **Autenticación**: `/api/v1/auth/*`
                    - **Leads**: `/api/v1/leads/*`
                    - **Clínicas**: `/api/v1/clinics/*`
                    - **Especialidades**: `/api/v1/medical-specialties/*`
                    
                    ### Roles:
                    - **ADMIN**: Acceso completo
                    - **USER**: Acceso limitado
                    """)
                .version(appVersion)
                .contact(new Contact()
                    .name("KiwiPay Development Team")
                    .email("alexander.castillo@kiwipay.pe"))
                .license(new License()
                    .name("KiwiPay License")
                    .url("https://kiwipay.pe/license"));
    }

    /**
     * Componentes de seguridad simplificados
     * Solo Bearer Token JWT
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication", bearerAuthScheme());
    }

    /**
     * Esquema de autenticación Bearer Token simplificado
     */
    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                    ### JWT Bearer Token
                    
                    **Formato:** `Authorization: Bearer <jwt-token>`
                    
                    **Cómo obtener tu token:**
                    1. Usar endpoint `POST /api/v1/auth/login`
                    2. Enviar `username` y `password`
                    3. Copiar el `token` de la respuesta
                    4. Usar ese token aquí (sin escribir "Bearer")
                    
                    **Ejemplo de token:**
                    ```
                    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTYwOTQ1OTIwMCwiZXhwIjoxNjA5NDYyODAwfQ.signature
                    ```
                    
                    **Importante:**
                    - Los tokens expiran automáticamente
                    - Si obtienes error 401, necesitas un nuevo token
                    - Solo pega el token, NO escribas "Bearer"
                    """);
    }
} 