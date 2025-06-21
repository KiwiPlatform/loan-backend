package com.kiwipay.kiwipay_loan_backend.leads.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for creating a new lead from the medical form.
 * Maps exactly to the form fields with proper validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLeadRequest {

    @Size(max = 255, message = "El nombre del recepcionista no puede exceder 255 caracteres")
    private String receptionistName;

    @Size(max = 255, message = "El nombre del cliente no puede exceder 255 caracteres")
    private String clientName;

    @NotNull(message = "La clínica es obligatoria")
    @Positive(message = "El ID de la clínica debe ser positivo")
    private Long clinicId;

    @NotNull(message = "La especialidad médica es obligatoria")
    @Positive(message = "El ID de la especialidad debe ser positivo")
    private Long medicalSpecialtyId;

    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener exactamente 8 dígitos")
    private String dni;

    @NotNull(message = "El ingreso mensual es obligatorio")
    @Positive(message = "El ingreso mensual debe ser mayor a 0")
    @DecimalMin(value = "0.01", message = "El ingreso mensual debe ser mayor a 0")
    private BigDecimal monthlyIncome;

    @NotNull(message = "El costo del tratamiento es obligatorio")
    @Positive(message = "El costo del tratamiento debe ser mayor a 0")
    @DecimalMin(value = "0.01", message = "El costo del tratamiento debe ser mayor a 0")
    private BigDecimal treatmentCost;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
    private String phone;

    @Email(message = "El formato del correo electrónico no es válido", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @Size(max = 255, message = "El correo electrónico no puede exceder 255 caracteres")
    private String email;
} 