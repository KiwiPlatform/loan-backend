package com.kiwipay.kiwipay_loan_backend.leads.service;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.AuthRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.request.RegisterRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.AuthResponse;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.RegisterResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.Role;
import com.kiwipay.kiwipay_loan_backend.leads.entity.User;
import com.kiwipay.kiwipay_loan_backend.leads.exception.BusinessException;
import com.kiwipay.kiwipay_loan_backend.leads.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación
 * Maneja el registro e inicio de sesión de usuarios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Registra un nuevo usuario en el sistema
     * BUENAS PRÁCTICAS: No devuelve token JWT - requiere login separado
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Iniciando registro de usuario: {}", request.getUsername());
        
        // Validar que el usuario no exista
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username ya existe: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email ya está registrado: " + request.getEmail());
        }
        
        // Determinar rol (por defecto USER si no se especifica)
        Role userRole = Role.USER;
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                userRole = Role.valueOf(request.getRole().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                log.warn("Rol inválido especificado: {}, usando USER por defecto", request.getRole());
                userRole = Role.USER;
            }
        }
        
        // Crear nuevo usuario
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .enabled(true)
                .build();
        
        // Guardar usuario
        userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", user.getUsername());
        
        // Devolver respuesta SIN token (buenas prácticas de seguridad)
        return RegisterResponse.builder()
                .message("Usuario registrado exitosamente")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .registeredAt(LocalDateTime.now())
                .nextStep("Para acceder al sistema, use el endpoint POST /api/v1/auth/login con sus credenciales")
                .build();
    }
    
    /**
     * Autentica un usuario existente
     */
    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Iniciando autenticación de usuario: {}", request.getUsername());
        
        try {
            // Autenticar credenciales
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // Buscar usuario
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + request.getUsername()));
            
            // Verificar que el usuario esté habilitado
            if (!user.isEnabled()) {
                throw new BusinessException("Usuario deshabilitado: " + request.getUsername());
            }
            
            // Generar token JWT
            var jwtToken = jwtService.generateToken(user);
            log.info("Usuario autenticado exitosamente: {}", user.getUsername());
            
            return AuthResponse.builder()
                    .token(jwtToken)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .expiresIn(jwtService.getExpirationTime())
                    .build();
                    
        } catch (AuthenticationException e) {
            log.error("Error de autenticación para usuario: {}", request.getUsername(), e);
            throw new BusinessException("Credenciales inválidas");
        }
    }
} 