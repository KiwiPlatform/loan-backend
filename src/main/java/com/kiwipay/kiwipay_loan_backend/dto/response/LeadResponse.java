package com.kiwipay.kiwipay_loan_backend.dto.response;

import com.kiwipay.kiwipay_loan_backend.entity.LeadStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for lead list response.
 * Contains essential information for the leads table view.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadResponse {
    
    private Long id;
    private String clientName;
    private String dni;
    private String clinicName;
    private String medicalSpecialtyName;
    private BigDecimal treatmentCost;
    private String phone;
    private LeadStatus status;
    private LocalDateTime createdAt;
} 