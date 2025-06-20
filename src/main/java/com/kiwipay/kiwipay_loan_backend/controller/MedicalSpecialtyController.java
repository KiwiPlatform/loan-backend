package com.kiwipay.kiwipay_loan_backend.controller;

import com.kiwipay.kiwipay_loan_backend.entity.MedicalSpecialty;
import com.kiwipay.kiwipay_loan_backend.repository.MedicalSpecialtyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Medical Specialty management.
 * Provides endpoints to retrieve medical specialty information.
 */
@RestController
@RequestMapping("/api/v1/medical-specialties")
@RequiredArgsConstructor
@Tag(name = "Medical Specialties", description = "Gestión de especialidades médicas")
public class MedicalSpecialtyController {

    private final MedicalSpecialtyRepository medicalSpecialtyRepository;

    @GetMapping
    @Operation(summary = "Get all active medical specialties", 
               description = "Returns a list of all active medical specialties for the dropdown selector")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<List<MedicalSpecialty>> getActiveMedicalSpecialties(
            @RequestParam(required = false) String category) {
        
        List<MedicalSpecialty> specialties;
        if (category != null && !category.isEmpty()) {
            specialties = medicalSpecialtyRepository.findByCategoryAndActiveTrue(category);
        } else {
            specialties = medicalSpecialtyRepository.findByActiveTrue();
        }
        
        return ResponseEntity.ok(specialties);
    }
} 