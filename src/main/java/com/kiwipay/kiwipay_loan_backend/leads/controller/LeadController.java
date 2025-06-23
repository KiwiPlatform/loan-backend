package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.CreateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.request.UpdateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.LeadDetailResponse;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.LeadResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import com.kiwipay.kiwipay_loan_backend.leads.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.DevAuth;
import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.AdaptiveSecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for Lead management.
 * Handles all lead-related HTTP requests.
 * 
 * CONFIGURACIÓN DE SEGURIDAD POR PERFIL:
 * - DEV: Sin JWT - Solo clave secreta de Swagger
 * - STAGING: Clave secreta + JWT (doble capa)
 * - PROD: Solo JWT (Swagger deshabilitado)
 */
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leads", description = """
    ## Gestión de Leads de Préstamos Médicos
    
    ### Seguridad por Entorno:
    - **DEV**: Solo clave secreta de Swagger (sin JWT)
    - **STAGING**: Clave secreta + autenticación JWT
    - **PROD**: Solo autenticación JWT (Swagger deshabilitado)
    
    ### Funcionalidades:
    - Crear leads desde formularios web
    - Consultar leads con filtros avanzados
    - Actualizar información completa
    - Gestión de estados del proceso
    - Exportación de datos para análisis
    """)
@DevAuth
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Create a new lead", description = """
        ### CREAR NUEVO LEAD
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Crea un nuevo lead desde los datos del formulario médico.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lead created successfully",
                content = @Content(schema = @Schema(implementation = LeadDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Lead with DNI already exists")
    })
    public ResponseEntity<LeadDetailResponse> createLead(
            @Valid @RequestBody CreateLeadRequest request) {
        log.info("Creating new lead with DNI: {}", request.getDni());
        LeadDetailResponse response = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Get paginated list of leads", description = """
        ### OBTENER LEADS PAGINADOS
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Obtiene una lista paginada de leads con filtros opcionales.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<LeadResponse>> getLeads(
            @Parameter(description = "Filter by lead status")
            @RequestParam(required = false) LeadStatus status,
            
            @Parameter(description = "Filter by clinic ID")
            @RequestParam(required = false) Long clinicId,
            
            @Parameter(description = "Filter by start date")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "Filter by end date")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable) {
        
        log.debug("Fetching leads with filters - status: {}, clinicId: {}", status, clinicId);
        Page<LeadResponse> leads = leadService.getLeads(status, clinicId, startDate, endDate, pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Get lead by ID", description = """
        ### OBTENER LEAD POR ID
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Obtiene información detallada de un lead específico.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead found",
                content = @Content(schema = @Schema(implementation = LeadDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    public ResponseEntity<LeadDetailResponse> getLeadById(
            @Parameter(description = "Lead ID", required = true)
            @PathVariable Long id) {
        log.info("Fetching lead with ID: {}", id);
        LeadDetailResponse response = leadService.getLeadById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Update lead status", description = """
        ### ACTUALIZAR STATUS DEL LEAD
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Actualiza el estado de un lead específico.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully",
                content = @Content(schema = @Schema(implementation = LeadDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    public ResponseEntity<LeadDetailResponse> updateLeadStatus(
            @Parameter(description = "Lead ID", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "New status", required = true)
            @RequestParam LeadStatus status) {
        log.info("Updating lead {} status to: {}", id, status);
        LeadDetailResponse response = leadService.updateLeadStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Update complete lead", description = """
        ### ACTUALIZAR LEAD COMPLETO
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Actualiza toda la información de un lead específico.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lead updated successfully",
                content = @Content(schema = @Schema(implementation = LeadDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Lead not found"),
        @ApiResponse(responseCode = "409", description = "DNI already exists for another lead")
    })
    public ResponseEntity<LeadDetailResponse> updateLead(
            @Parameter(description = "Lead ID", required = true)
            @PathVariable Long id,
            
            @Valid @RequestBody UpdateLeadRequest request) {
        log.info("Updating complete lead with ID: {}", id);
        LeadDetailResponse response = leadService.updateLead(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @AdaptiveSecurity(adminOnly = true)
    @Operation(
        summary = "Get all leads", 
        description = """
            ### OBTENER TODOS LOS LEADS (ADMINISTRATIVO)
            
            **CONFIGURACIÓN POR ENTORNO:**
            - **DEV**: Público (sin autenticación)
            - **STAGING/PROD**: Solo administradores (requiere JWT + rol ADMIN)
            
            **Advertencias:**
            - Puede retornar grandes volúmenes de datos
            - Usar con precaución en producción
            - Considerar usar endpoint paginado para datasets grandes
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista completa de leads obtenida exitosamente",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Permisos de administrador requeridos (STAGING/PROD)")
    })
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        log.info("Fetching all leads without pagination");
        List<LeadResponse> leads = leadService.getAllLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/all/filtered")
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Get all leads with filters", description = """
        ### OBTENER TODOS LOS LEADS CON FILTROS
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Obtiene todos los leads con filtros opcionales pero sin paginación.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<LeadResponse>> getAllLeadsFiltered(
            @Parameter(description = "Filter by lead status")
            @RequestParam(required = false) LeadStatus status,
            
            @Parameter(description = "Filter by clinic ID")
            @RequestParam(required = false) Long clinicId,
            
            @Parameter(description = "Filter by start date")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "Filter by end date")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.debug("Fetching all leads with filters - status: {}, clinicId: {}", status, clinicId);
        List<LeadResponse> leads = leadService.getAllLeadsFiltered(status, clinicId, startDate, endDate);
        return ResponseEntity.ok(leads);
    }
} 