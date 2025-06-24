package com.kiwipay.kiwipay_loan_backend.leads.dto.request;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO para recibir leads desde la API/middleware de Squarespace.
 * Los montos vienen como números tal como los envía la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SquarespaceBackendRequest {
    
    // Campos tal como vienen de la API
    private String receptionistName; // "Juan Pérez"
    private String sede;             // "Lima", "Callao", etc.
    private String clientName;       // "María García"
    private String dni;              // "12345678"
    private BigDecimal monthlyIncome;    // 3000.00 (como número)
    private BigDecimal treatmentCost;    // 5000.00 (como número)
    private String phone;            // "987654321"
    private String source;           // "squarespace"
} 