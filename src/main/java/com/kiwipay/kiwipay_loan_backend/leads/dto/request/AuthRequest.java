package com.kiwipay.kiwipay_loan_backend.leads.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para peticiones de autenticación (login)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición de autenticación para login")
public class AuthRequest {
    
    @NotBlank(message = "Username es requerido")
    @Schema(description = "Nombre de usuario", example = "admin", required = true)
    private String username;
    
    @NotBlank(message = "Password es requerido")
    @Schema(description = "Contraseña del usuario", example = "admin123", required = true)
    private String password;
} 