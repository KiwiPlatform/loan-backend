package com.kiwipay.kiwipay_loan_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing a loan lead from the medical form.
 */
@Entity
@Table(name = "leads", indexes = {
    @Index(name = "idx_leads_dni", columnList = "dni"),
    @Index(name = "idx_leads_created_at", columnList = "created_at"),
    @Index(name = "idx_leads_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @Column(name = "receptionist_name", nullable = false, length = 255)
    private String receptionistName;

    @Column(name = "client_name", nullable = false, length = 255)
    private String clientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_specialty_id", nullable = false)
    private MedicalSpecialty medicalSpecialty;

    @Column(name = "dni", nullable = false, length = 8)
    private String dni;

    @Column(name = "monthly_income", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "treatment_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal treatmentCost;

    @Column(name = "phone", nullable = false, length = 9)
    private String phone;

    @Column(name = "email", length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private LeadStatus status = LeadStatus.NUEVO;

    @Column(name = "origin", nullable = false, length = 50)
    @Builder.Default
    private String origin = "WEB";
} 