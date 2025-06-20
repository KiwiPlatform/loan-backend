package com.kiwipay.kiwipay_loan_backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a medical specialty.
 */
@Entity
@Table(name = "medical_specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalSpecialty extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
} 