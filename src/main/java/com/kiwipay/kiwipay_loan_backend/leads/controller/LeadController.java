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

import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.RequireAuthAny;
import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.RequireAdminAuth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for Lead management.
 * Handles all lead-related HTTP requests.
 */
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leads", description = """
    ## Gestión de Leads de Préstamos Médicos
    
    ### Seguridad:
    - Autenticación requerida en todos los endpoints
    - Datos sensibles encriptados (DNI, teléfonos)
    - Validación exhaustiva de entrada
    - Rate limiting aplicado
    - Logs de auditoría completos
    
    ### Funcionalidades:
    - Crear leads desde formularios web
    - Consultar leads con filtros avanzados
    - Actualizar información completa
    - Gestión de estados del proceso
    - Exportación de datos para análisis
    
    ### Importante:
    - Todos los endpoints requieren autenticación
    - Los datos personales están protegidos según GDPR
    - Se aplica rate limiting por usuario/IP
    """)
@RequireAuthAny
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @Operation(summary = "Create a new lead", description = "Creates a new lead from the medical form data")
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
    @Operation(summary = "Get paginated list of leads", description = "Returns a paginated list of leads with optional filters")
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
    @Operation(summary = "Get lead by ID", description = "Returns detailed information of a specific lead")
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
    @Operation(summary = "Update lead status", description = "Updates the status of a specific lead")
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
    @Operation(summary = "Update complete lead", description = "Updates all information of a specific lead")
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
    @Operation(
        summary = "Get all leads", 
        description = """
            ### ENDPOINT ADMINISTRATIVO
            
            Retorna TODOS los leads sin paginación.
            
            **Advertencias:**
            - Solo para administradores
            - Puede retornar grandes volúmenes de datos
            - Usar con precaución en producción
            - Considerar usar endpoint paginado para datasets grandes
            
            **Seguridad:**
            - Requiere autenticación de administrador
            - Datos sensibles están encriptados
            - Acceso registrado en logs de auditoría
            
            **Alternativas recomendadas:**
            - GET /api/v1/leads (paginado)
            - GET /api/v1/leads/all/filtered (con filtros)
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista completa de leads obtenida exitosamente",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Permisos de administrador requeridos"),
        @ApiResponse(responseCode = "429", description = "Rate limit excedido - Demasiadas solicitudes")
    })
    @RequireAdminAuth
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        log.info("Fetching all leads without pagination");
        List<LeadResponse> leads = leadService.getAllLeads();
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/all/filtered")
    @Operation(summary = "Get all leads with filters", description = "Returns all leads with optional filters but without pagination")
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