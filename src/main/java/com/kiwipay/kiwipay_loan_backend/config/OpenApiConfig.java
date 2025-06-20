package com.kiwipay.kiwipay_loan_backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration delegated to OpenApiSecurityConfig
 * 
 * @deprecated Use OpenApiSecurityConfig for complete security-aware configuration
 * 
 * Esta clase se mantiene para compatibilidad pero la configuración real
 * se maneja en OpenApiSecurityConfig con mejores prácticas de seguridad.
 */
@Configuration
@Deprecated(since = "1.1.0", forRemoval = true)
public class OpenApiConfig {
    
    // Configuración movida a OpenApiSecurityConfig
    // para implementar mejores prácticas de seguridad empresarial
}