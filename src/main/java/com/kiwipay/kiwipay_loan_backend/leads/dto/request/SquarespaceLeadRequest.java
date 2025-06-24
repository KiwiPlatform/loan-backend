package com.kiwipay.kiwipay_loan_backend.leads.dto.request;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO específico para formulario de Squarespace.
 * Solo contiene los campos exactos de la imagen.
 * Sin validaciones para máxima flexibilidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SquarespaceLeadRequest {
    
    // Nombres y Apellidos Recepcionista
    private String receptionistName;
    
    // Sede (enviada como string desde el formulario)
    private String sede;
    
    // Nombres y Apellidos Cliente
    private String clientName;
    
    // N° DNI (obligatorio en el formulario)
    private String dni;
    
    // Ingreso Mensual Promedio S/. (obligatorio)
    private BigDecimal monthlyIncome;
    
    // Costo Aprox. Tratamiento S/. (obligatorio)
    private BigDecimal treatmentCost;
    
    // Teléfono (obligatorio)
    private String phone;
} 