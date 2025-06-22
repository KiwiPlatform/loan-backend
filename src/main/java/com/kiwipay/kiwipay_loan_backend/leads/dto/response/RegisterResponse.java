package com.kiwipay.kiwipay_loan_backend.leads.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta del registro de usuario
 * No incluye token JWT - mejores prácticas de seguridad
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    
    /**
     * Mensaje de confirmación del registro
     */
    private String message;
    
    /**
     * Username del usuario registrado
     */
    private String username;
    
    /**
     * Email del usuario registrado
     */
    private String email;
    
    /**
     * Rol asignado al usuario
     */
    private String role;
    
    /**
     * Fecha y hora del registro
     */
    private LocalDateTime registeredAt;
    
    /**
     * Instrucciones para el siguiente paso
     */
    private String nextStep;
} 