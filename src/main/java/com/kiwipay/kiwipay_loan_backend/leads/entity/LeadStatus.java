package com.kiwipay.kiwipay_loan_backend.leads.entity;

/**
 * Enum representing the possible statuses of a lead.
 */
public enum LeadStatus {
    NUEVO("Nuevo"),
    CONTACTADO("Contactado"),
    EN_EVALUACION("En Evaluación"),
    PRE_APROBADO("Pre-aprobado"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    DESEMBOLSADO("Desembolsado");

    private final String displayName;

    LeadStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 