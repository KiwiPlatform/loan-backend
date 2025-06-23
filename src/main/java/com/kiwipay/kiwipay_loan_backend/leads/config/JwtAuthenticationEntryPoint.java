package com.kiwipay.kiwipay_loan_backend.leads.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Punto de entrada para manejar errores de autenticación JWT
 * Se ejecuta cuando una petición no autenticada intenta acceder a un recurso protegido
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        
        log.warn("Acceso no autorizado a: {} desde IP: {}", 
                request.getRequestURI(), 
                getClientIP(request));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "Acceso no autorizado. Token JWT requerido.");
        errorDetails.put("path", request.getRequestURI());
        
        // Información adicional sobre qué se esperaba
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            errorDetails.put("details", "Header 'Authorization' faltante");
        } else if (!authHeader.startsWith("Bearer ")) {
            errorDetails.put("details", "Header 'Authorization' debe empezar con 'Bearer '");
        } else {
            errorDetails.put("details", "Token JWT inválido o expirado");
        }
        
        // Instrucciones para el cliente
        errorDetails.put("howToFix", "Incluya un token JWT válido en el header: Authorization: Bearer <token>");
        
        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
    
    /**
     * Obtiene la IP del cliente considerando proxies
     */
    private String getClientIP(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        
        String realIP = request.getHeader("X-Real-IP");
        if (realIP != null && !realIP.isEmpty()) {
            return realIP;
        }
        
        return request.getRemoteAddr();
    }
} 