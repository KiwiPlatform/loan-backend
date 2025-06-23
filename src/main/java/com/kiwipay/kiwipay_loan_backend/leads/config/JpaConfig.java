package com.kiwipay.kiwipay_loan_backend.leads.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration for the application.
 * Enables repositories and transaction management.
 * Note: JPA Auditing is configured in AuditConfig.java
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.kiwipay.kiwipay_loan_backend.leads.repository")
@EnableTransactionManagement
public class JpaConfig {
    // Configuration is handled by annotations
    // JPA Auditing is configured separately in AuditConfig
} 