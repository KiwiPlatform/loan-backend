package com.kiwipay.kiwipay_loan_backend.controller;

import com.kiwipay.kiwipay_loan_backend.service.ExcelPopulateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para ejecutar el proceso de populate de la base de datos desde Excel
 */
@RestController
@RequestMapping("/api/v1/populate")
@RequiredArgsConstructor
@Slf4j
public class PopulateController {
    
    private final ExcelPopulateService excelPopulateService;
    
    /**
     * Ejecuta el proceso de populate desde el archivo Excel
     */
    @PostMapping("/excel")
    public ResponseEntity<Map<String, String>> populateFromExcel() {
        try {
            log.info("Iniciando populate desde Excel via REST API");
            
            excelPopulateService.populateFromExcel();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Base de datos poblada exitosamente desde Excel"
            ));
            
        } catch (Exception e) {
            log.error("Error durante populate: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Error poblando la base de datos: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Endpoint de prueba para verificar que el controlador funciona
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "ready",
            "message", "Servicio de populate disponible"
        ));
    }
} 