package com.kiwipay.kiwipay_loan_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuration for JPA Auditing.
 * Tracks who and when made changes to entities.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Implementation of AuditorAware to provide current user information.
     */
    public static class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("SYSTEM");
            }
            
            // En producción, aquí obtienes el usuario del JWT token
            return Optional.of(authentication.getName());
        }
    }
} 