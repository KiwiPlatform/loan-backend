package com.kiwipay.kiwipay_loan_backend.leads.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad específica para leads provenientes de Squarespace.
 * Tabla separada para mejor organización y flexibilidad.
 */
@Entity
@Table(name = "squarespace_leads", indexes = {
    @Index(name = "idx_squarespace_dni", columnList = "dni"),
    @Index(name = "idx_squarespace_created_at", columnList = "created_at"),
    @Index(name = "idx_squarespace_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SquarespaceLead extends BaseEntity {

    @Column(name = "receptionist_name", length = 255)
    private String receptionistName; // Nombres y Apellidos Recepcionista

    @Column(name = "sede", length = 100)
    private String sede; // Sede como string (exactamente como viene del formulario)

    @Column(name = "client_name", length = 255)
    private String clientName; // Nombres y Apellidos Cliente

    @Column(name = "dni", length = 8)
    private String dni; // N° DNI

    @Column(name = "monthly_income", precision = 10, scale = 2)
    private BigDecimal monthlyIncome; // Ingreso Mensual Promedio S/.

    @Column(name = "treatment_cost", precision = 10, scale = 2)
    private BigDecimal treatmentCost; // Costo Aprox. Tratamiento S/.

    @Column(name = "phone", length = 15)
    private String phone; // Teléfono

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private LeadStatus status = LeadStatus.NUEVO; // Estado del lead

    @Column(name = "processed", nullable = false)
    @Builder.Default
    private Boolean processed = false; // Si ya fue procesado/revisado

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Notas adicionales del equipo

    @Column(name = "source_url", length = 500)
    private String sourceUrl; // URL de origen del formulario

    // Campos específicos de Squarespace
    @Column(name = "form_submission_id", length = 100)
    private String formSubmissionId; // ID del envío del formulario

    @Column(name = "user_agent", length = 500)
    private String userAgent; // Info del navegador

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // IP del usuario
} 