package com.kiwipay.kiwipay_loan_backend.leads.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de autenticación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthResponse {
    
    @Schema(description = "Token JWT generado", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Tipo de token", example = "Bearer")
    @Builder.Default
    private String type = "Bearer";
    
    @Schema(description = "Nombre de usuario", example = "admin")
    private String username;
    
    @Schema(description = "Email del usuario", example = "admin@kiwipay.pe")
    private String email;
    
    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String role;
    
    @Schema(description = "Tiempo de expiración en segundos", example = "86400")
    private Long expiresIn;
} 