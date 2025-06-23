package com.kiwipay.kiwipay_loan_backend.leads.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para peticiones de registro de nuevos usuarios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición de registro de nuevo usuario")
public class RegisterRequest {
    
    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "nuevo_usuario", required = true)
    private String username;
    
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    @Schema(description = "Email del usuario", example = "usuario@kiwipay.pe", required = true)
    private String email;
    
    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña del usuario (mínimo 6 caracteres)", example = "password123", required = true)
    private String password;
    
    @Schema(description = "Rol del usuario", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role = "USER"; // Valor por defecto
} 