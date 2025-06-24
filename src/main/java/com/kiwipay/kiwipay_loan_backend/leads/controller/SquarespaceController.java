package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.SquarespaceLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.SquarespaceLeadResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import com.kiwipay.kiwipay_loan_backend.leads.service.SquarespaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller exclusivo para gestionar leads de Squarespace.
 * Incluye endpoints de recepción y gestión administrativa.
 */
@RestController
@RequestMapping("/api/v1/squarespace")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Squarespace", description = """
    ## Gestión Completa de Leads de Squarespace
    
    ### Funcionalidades:
    - **Recepción**: Endpoint público para recibir leads del formulario
    - **Gestión**: Endpoints administrativos para gestionar leads
    - **Análisis**: Estadísticas y reportes específicos
    - **Tracking**: Seguimiento del procesamiento de leads
    """)
public class SquarespaceController {

    private final SquarespaceService squarespaceService;

    // ========================================
    // ENDPOINT PÚBLICO PARA RECIBIR LEADS
    // ========================================
    
    @PostMapping("/lead")
    @Operation(summary = "🔥 RECIBIR LEAD DESDE SQUARESPACE", description = """
        ### ENDPOINT PÚBLICO PARA FORMULARIOS
        
        Recibe ÚNICAMENTE los campos del formulario de tratamiento de salud:
        - **receptionistName**: Nombres y Apellidos Recepcionista
        - **sede**: Sede seleccionada (texto libre)
        - **clientName**: Nombres y Apellidos Cliente
        - **dni**: N° DNI
        - **monthlyIncome**: Ingreso Mensual Promedio S/.
        - **treatmentCost**: Costo Aprox. Tratamiento S/.
        - **phone**: Teléfono
        
        ✅ **Sin validaciones estrictas** - Máxima compatibilidad
        ✅ **Tabla independiente** - No interfiere con otros leads
        ✅ **Logs detallados** - Registro completo de todos los campos
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lead recibido y guardado exitosamente",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Error procesando los datos del formulario")
    })
    public ResponseEntity<Map<String, Object>> receiveSquarespaceLead(
            @RequestBody SquarespaceLeadRequest request) {
        
        log.info("=== NUEVO LEAD DE SQUARESPACE ===");
        log.info("📋 Recepcionista: {}", request.getReceptionistName());
        log.info("🏢 Sede: {}", request.getSede());
        log.info("👤 Cliente: {}", request.getClientName());
        log.info("🆔 DNI: {}", request.getDni());
        log.info("💰 Ingreso Mensual: S/. {}", request.getMonthlyIncome());
        log.info("🏥 Costo Tratamiento: S/. {}", request.getTreatmentCost());
        log.info("📞 Teléfono: {}", request.getPhone());
        log.info("================================");
        
        try {
            Long leadId = squarespaceService.processSquarespaceLead(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("message", "¡Lead recibido exitosamente! Nos pondremos en contacto pronto.");
            response.put("leadId", leadId);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("✅ Lead #{} procesado exitosamente", leadId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("❌ Error procesando lead de Squarespace: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("ok", false);
            errorResponse.put("error", "Error interno del servidor. Por favor intenta nuevamente.");
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ========================================
    // ENDPOINTS ADMINISTRATIVOS
    // ========================================
    
    @GetMapping("/leads")
    @Operation(summary = "📊 Obtener leads de Squarespace con filtros", description = """
        ### GESTIÓN ADMINISTRATIVA
        
        Obtiene leads de Squarespace con filtros avanzados:
        - Por estado (NUEVO, EN_PROCESO, etc.)
        - Por sede
        - Por estado de procesamiento
        - Por rango de fechas
        - Paginación incluida
        """)
    public ResponseEntity<Page<SquarespaceLeadResponse>> getSquarespaceLeads(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) String sede,
            @RequestParam(required = false) Boolean processed,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable) {
        
        log.debug("Consultando leads de Squarespace - Estado: {}, Sede: {}, Procesado: {}", 
                 status, sede, processed);
        
        Page<SquarespaceLeadResponse> leads = squarespaceService.getSquarespaceLeads(
            status, sede, processed, startDate, endDate, pageable);
        
        return ResponseEntity.ok(leads);
    }
    
    @GetMapping("/leads/today")
    @Operation(summary = "📅 Leads de hoy", description = "Obtiene todos los leads recibidos hoy")
    public ResponseEntity<List<SquarespaceLeadResponse>> getTodaysLeads() {
        List<SquarespaceLeadResponse> leads = squarespaceService.getTodaysLeads();
        return ResponseEntity.ok(leads);
    }
    
    @GetMapping("/leads/stats")
    @Operation(summary = "📈 Estadísticas de leads", description = "Estadísticas generales de leads de Squarespace")
    public ResponseEntity<Map<String, Object>> getLeadStats() {
        Map<String, Object> stats = squarespaceService.getLeadStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @PatchMapping("/leads/{id}/process") 
    @Operation(summary = "✅ Marcar lead como procesado", description = "Marca un lead como procesado y opcionalmente agrega notas")
    public ResponseEntity<SquarespaceLeadResponse> markAsProcessed(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        
        SquarespaceLeadResponse response = squarespaceService.markAsProcessed(id, notes);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    @Operation(summary = "💚 Health check", description = "Verificar estado del servicio Squarespace")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("endpoint", "/api/v1/squarespace/lead");
        response.put("table", "squarespace_leads");
        response.put("message", "Servicio Squarespace funcionando correctamente");
        return ResponseEntity.ok(response);
    }
} 