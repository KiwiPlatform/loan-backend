package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.entity.MedicalSpecialty;
import com.kiwipay.kiwipay_loan_backend.leads.repository.MedicalSpecialtyRepository;
import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.AdaptiveSecurity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Medical Specialty management.
 * 
 * CONFIGURACIÓN DE SEGURIDAD POR PERFIL:
 * - DEV: Sin JWT - Solo clave secreta de Swagger
 * - STAGING: Clave secreta + JWT (doble capa)
 * - PROD: Solo JWT (Swagger deshabilitado)
 */
@RestController
@RequestMapping("/api/v1/medical-specialties")
@RequiredArgsConstructor
@Tag(name = "Medical Specialties", description = """
    ## Gestión de Especialidades Médicas
    
    ### Seguridad por Entorno:
    - **DEV**: Solo clave secreta de Swagger (sin JWT)
    - **STAGING**: Clave secreta + autenticación JWT
    - **PROD**: Solo autenticación JWT (Swagger deshabilitado)
    """)
public class MedicalSpecialtyController {

    private final MedicalSpecialtyRepository medicalSpecialtyRepository;

    @GetMapping
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Get all medical specialties", description = """
        ### OBTENER ESPECIALIDADES MÉDICAS
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Obtiene la lista completa de especialidades médicas disponibles.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de especialidades médicas obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<MedicalSpecialty>> getAllMedicalSpecialties() {
        List<MedicalSpecialty> specialties = medicalSpecialtyRepository.findAll();
        return ResponseEntity.ok(specialties);
    }

    @PostMapping
    @AdaptiveSecurity(adminOnly = true)
    @Operation(summary = "Create medical specialty", description = """
        ### CREAR ESPECIALIDAD MÉDICA (ADMINISTRATIVO)
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Solo administradores (requiere JWT + rol ADMIN)
        
        **Propósito:**
        Crea una nueva especialidad médica en el sistema.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especialidad médica creada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores (STAGING/PROD)")
    })
    public ResponseEntity<MedicalSpecialty> createMedicalSpecialty(@RequestBody MedicalSpecialty specialty) {
        MedicalSpecialty savedSpecialty = medicalSpecialtyRepository.save(specialty);
        return ResponseEntity.ok(savedSpecialty);
    }
} 