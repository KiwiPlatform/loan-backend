package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.entity.Clinic;
import com.kiwipay.kiwipay_loan_backend.leads.repository.ClinicRepository;
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
 * REST controller for Clinic management.
 * 
 * CONFIGURACIÓN DE SEGURIDAD POR PERFIL:
 * - DEV: Sin JWT - Solo clave secreta de Swagger
 * - STAGING: Clave secreta + JWT (doble capa)
 * - PROD: Solo JWT (Swagger deshabilitado)
 */
@RestController
@RequestMapping("/api/v1/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinics", description = """
    ## Gestión de Clínicas
    
    ### Seguridad por Entorno:
    - **DEV**: Solo clave secreta de Swagger (sin JWT)
    - **STAGING**: Clave secreta + autenticación JWT
    - **PROD**: Solo autenticación JWT (Swagger deshabilitado)
    """)
public class ClinicController {

    private final ClinicRepository clinicRepository;

    @GetMapping
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Get all clinics", description = """
        ### ENDPOINT ADAPTIVO POR ENTORNO
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Obtiene la lista completa de clínicas registradas en el sistema.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clínicas obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> clinics = clinicRepository.findAll();
        return ResponseEntity.ok(clinics);
    }

    @PostMapping
    @AdaptiveSecurity(adminOnly = true)
    @Operation(summary = "Create clinic", description = """
        ### ENDPOINT ADMINISTRATIVO ADAPTIVO
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Solo administradores (requiere JWT + rol ADMIN)
        
        **Propósito:**
        Crea una nueva clínica en el sistema.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clínica creada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores (STAGING/PROD)")
    })
    public ResponseEntity<Clinic> createClinic(@RequestBody Clinic clinic) {
        Clinic savedClinic = clinicRepository.save(clinic);
        return ResponseEntity.ok(savedClinic);
    }
} 