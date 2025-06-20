package com.kiwipay.kiwipay_loan_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for updating an existing lead.
 * All fields are optional to allow partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLeadRequest {

    @Size(max = 255, message = "El nombre del recepcionista no puede exceder 255 caracteres")
    private String receptionistName;

    @Size(max = 255, message = "El nombre del cliente no puede exceder 255 caracteres")
    private String clientName;

    @Positive(message = "El ID de la clínica debe ser positivo")
    private Long clinicId;

    @Positive(message = "El ID de la especialidad debe ser positivo")
    private Long medicalSpecialtyId;

    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;

    @Positive(message = "El ingreso mensual debe ser mayor a 0")
    @DecimalMin(value = "0.01", message = "El ingreso mensual debe ser mayor a 0")
    private BigDecimal monthlyIncome;

    @Positive(message = "El costo del tratamiento debe ser mayor a 0")
    @DecimalMin(value = "0.01", message = "El costo del tratamiento debe ser mayor a 0")
    private BigDecimal treatmentCost;

    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
    private String phone;

    @Email(message = "El formato del correo electrónico no es válido", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @Size(max = 255, message = "El correo electrónico no puede exceder 255 caracteres")
    private String email;

    // Status update (opcional)
    private String status;

    // Origin update (opcional)
    @Size(max = 50, message = "El origen no puede exceder 50 caracteres")
    private String origin;
} 