package com.kiwipay.kiwipay_loan_backend.leads.controller;

import com.kiwipay.kiwipay_loan_backend.leads.entity.Clinic;
import com.kiwipay.kiwipay_loan_backend.leads.repository.ClinicRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Clinic management.
 * Provides endpoints to retrieve clinic information.
 */
@RestController
@RequestMapping("/api/v1/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinics", description = "Gestión de clínicas y centros médicos")
public class ClinicController {

    private final ClinicRepository clinicRepository;

    @GetMapping
    @Operation(summary = "Get all active clinics", 
               description = "Returns a list of all active clinics for the dropdown selector")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<List<Clinic>> getActiveClinics() {
        List<Clinic> clinics = clinicRepository.findByActiveTrue();
        return ResponseEntity.ok(clinics);
    }
} 