package com.kiwipay.kiwipay_loan_backend.leads.dto.response;

import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for detailed lead response.
 * Contains complete information for the lead detail view.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadDetailResponse {
    
    // Basic Information
    private Long id;
    private String receptionistName;
    private String clientName;
    private String dni;
    private String phone;
    private String email;
    
    // Financial Information
    private BigDecimal monthlyIncome;
    private BigDecimal treatmentCost;
    
    // Medical Information
    private Long clinicId;
    private String clinicName;
    private Long medicalSpecialtyId;
    private String medicalSpecialtyName;
    
    // Status Information
    private LeadStatus status;
    private String origin;
    
    // Audit Information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 