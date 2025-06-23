package com.kiwipay.kiwipay_loan_backend.leads.config;

import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.AdaptiveSecurity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspecto de Seguridad Adaptiva para manejar autorizaciones condicionales según el perfil activo.
 * 
 * LÓGICA DE AUTORIZACIÓN:
 * - DEV: Siempre permite acceso (sin validación)
 * - STAGING/PROD: Valida JWT + roles según la configuración @AdaptiveSecurity
 * 
 * @author alexander.castillo@kiwipay.pe
 * @version 1.0.0
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class AdaptiveSecurityAspect {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * Intercepta métodos anotados con @AdaptiveSecurity y aplica autorización condicional
     */
    @Before("@annotation(adaptiveSecurity)")
    public void checkAdaptiveSecurity(AdaptiveSecurity adaptiveSecurity) {
        log.debug("AdaptiveSecurity check - Profile: {}, AdminOnly: {}, Roles: {}", 
                  activeProfile, adaptiveSecurity.adminOnly(), String.join(",", adaptiveSecurity.roles()));

        // En desarrollo, permitir todo
        if ("dev".equals(activeProfile)) {
            log.debug("DEV profile detected - Access granted without authentication");
            return;
        }

        // En staging/prod, validar autenticación y autorización
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Access denied - No authentication found (Profile: {})", activeProfile);
            throw new AccessDeniedException("Autenticación requerida en entorno " + activeProfile.toUpperCase());
        }

        // Si es solo para administradores
        if (adaptiveSecurity.adminOnly()) {
            if (!hasRole(authentication, "ADMIN")) {
                log.warn("Access denied - Admin role required (User: {}, Profile: {})", 
                         authentication.getName(), activeProfile);
                throw new AccessDeniedException("Acceso restringido solo para administradores");
            }
            log.debug("Admin access granted (User: {}, Profile: {})", authentication.getName(), activeProfile);
            return;
        }

        // Verificar roles permitidos
        boolean hasRequiredRole = false;
        for (String role : adaptiveSecurity.roles()) {
            if (hasRole(authentication, role)) {
                hasRequiredRole = true;
                break;
            }
        }

        if (!hasRequiredRole) {
            log.warn("Access denied - Required roles: {} (User: {}, Profile: {})", 
                     String.join(",", adaptiveSecurity.roles()), authentication.getName(), activeProfile);
            throw new AccessDeniedException("Roles insuficientes. Roles requeridos: " + String.join(", ", adaptiveSecurity.roles()));
        }

        log.debug("Role-based access granted (User: {}, Profile: {})", authentication.getName(), activeProfile);
    }

    /**
     * Verifica si el usuario autenticado tiene el rol especificado
     */
    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
} 