package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.AuthRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.request.RegisterRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.AuthResponse;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.RegisterResponse;
import com.kiwipay.kiwipay_loan_backend.leads.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación
 * Maneja el registro e inicio de sesión de usuarios
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = """
    ## Sistema de Autenticación JWT - Buenas Prácticas de Seguridad
    
    ### 🔐 Flujo de autenticación:
    1. **REGISTRO**: POST /register (NO devuelve token)
    2. **LOGIN**: POST /login (devuelve token JWT)
    
    ### 📋 Endpoints disponibles:
    - **POST /register**: Registrar nuevo usuario (sin token por seguridad)
    - **POST /login**: Iniciar sesión y obtener token JWT
    
    ### ✅ Características de seguridad:
    - Separación de registro y autenticación (mejores prácticas)
    - Autenticación basada en JWT
    - Contraseñas hasheadas con BCrypt
    - Tokens con expiración configurable
    - Validación de entrada completa
    - Manejo de errores detallado
    
    ### 👥 Roles disponibles:
    - **ADMIN**: Acceso completo al sistema
    - **USER**: Acceso limitado según permisos
    
    ### 🚀 Cómo empezar:
    1. Usa POST /register para crear tu cuenta
    2. Usa POST /login para obtener tu token JWT
    3. Incluye el token en el header: `Authorization: Bearer <token>`
    4. Haz clic en 'Authorize' en Swagger para configurar el token
    
    ### ⚠️ Importante:
    - El registro NO devuelve token (buena práctica de seguridad)
    - Debes hacer login por separado para obtener el token
    - Los usuarios nuevos se registran como USER por defecto
    """)
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = """
            Crea una nueva cuenta de usuario en el sistema.
            
            **IMPORTANTE**: Este endpoint NO devuelve token JWT por seguridad.
            Después del registro exitoso, usa el endpoint POST /login para obtener tu token.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente - Sin token JWT"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Usuario o email ya existe")
    })
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Solicitud de registro recibida para usuario: {}", request.getUsername());
        RegisterResponse response = authService.register(request);
        log.info("Usuario registrado exitosamente: {} - Requiere login separado", request.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica las credenciales del usuario y genera un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Usuario deshabilitado")
    })
    public ResponseEntity<AuthResponse> authenticate(
            @Valid @RequestBody AuthRequest request
    ) {
        log.info("Solicitud de login recibida para usuario: {}", request.getUsername());
        AuthResponse response = authService.authenticate(request);
        log.info("Usuario autenticado y token generado para: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
} 