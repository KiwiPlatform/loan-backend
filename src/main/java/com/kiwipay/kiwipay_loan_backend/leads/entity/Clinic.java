package com.kiwipay.kiwipay_loan_backend.leads.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a medical clinic.
 */
@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinic extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
} 