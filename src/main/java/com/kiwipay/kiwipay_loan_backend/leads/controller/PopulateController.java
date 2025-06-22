package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.service.ExcelPopulateService;
import com.kiwipay.kiwipay_loan_backend.leads.config.SwaggerSecurityAnnotations.AdaptiveSecurity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for Data Population operations.
 * 
 * CONFIGURACIÓN DE SEGURIDAD POR PERFIL:
 * - DEV: Sin JWT - Solo clave secreta de Swagger
 * - STAGING: Clave secreta + JWT (doble capa)
 * - PROD: Solo JWT (Swagger deshabilitado)
 */
@RestController
@RequestMapping("/api/v1/populate")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Population", description = """
    ## Gestión de Población de Datos
    
    ### Seguridad por Entorno:
    - **DEV**: Solo clave secreta de Swagger (sin JWT)
    - **STAGING**: Clave secreta + autenticación JWT (solo ADMIN)
    - **PROD**: Solo autenticación JWT (solo ADMIN, Swagger deshabilitado)
    """)
public class PopulateController {

    private final ExcelPopulateService excelPopulateService;

    @PostMapping("/excel")
    @AdaptiveSecurity(adminOnly = true)
    @Operation(
        summary = "Populate database from Excel", 
        description = """
            ### POBLAR BASE DE DATOS DESDE EXCEL (ADMINISTRATIVO)
            
            **CONFIGURACIÓN POR ENTORNO:**
            - **DEV**: Público (sin autenticación)
            - **STAGING/PROD**: Solo administradores (requiere JWT + rol ADMIN)
            
            **Propósito:**
            Pobla la base de datos desde un archivo Excel predefinido.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos poblados exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Permisos de administrador requeridos (STAGING/PROD)"),
        @ApiResponse(responseCode = "500", description = "Error durante el proceso de población")
    })
    public ResponseEntity<Map<String, Object>> populateFromExcel() {
        log.info("Starting data population from Excel file");
        try {
            excelPopulateService.populateFromExcel();
            Map<String, Object> result = Map.of(
                "status", "success",
                "message", "Data populated successfully from Excel file"
            );
            log.info("Data population completed successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during data population", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to populate data: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    @AdaptiveSecurity(roles = {"USER", "ADMIN"})
    @Operation(summary = "Get population status", description = """
        ### OBTENER ESTADO DE POBLACIÓN
        
        **CONFIGURACIÓN POR ENTORNO:**
        - **DEV**: Público (sin autenticación)
        - **STAGING/PROD**: Requiere autenticación JWT (USER o ADMIN)
        
        **Propósito:**
        Obtiene el estado actual de la población de la base de datos.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido (STAGING/PROD)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, Object>> getPopulationStatus() {
        log.debug("Getting population status");
        
        // Aquí puedes implementar lógica para verificar el estado de la población
        Map<String, Object> status = Map.of(
            "status", "ready",
            "message", "Database is ready for population operations"
        );
        
        return ResponseEntity.ok(status);
    }
} 