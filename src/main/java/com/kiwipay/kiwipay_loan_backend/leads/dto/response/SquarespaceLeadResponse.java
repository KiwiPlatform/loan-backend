package com.kiwipay.kiwipay_loan_backend.leads.dto.response;

import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para leads de Squarespace.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SquarespaceLeadResponse {
    
    private Long id;
    private String receptionistName;
    private String sede;
    private String clientName;
    private String dni;
    private BigDecimal monthlyIncome;
    private BigDecimal treatmentCost;
    private String phone;
    private LeadStatus status;
    private Boolean processed;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 