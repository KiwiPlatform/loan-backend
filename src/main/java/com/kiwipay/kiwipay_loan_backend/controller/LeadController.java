package com.kiwipay.kiwipay_loan_backend.controller;

import com.kiwipay.kiwipay_loan_backend.dto.request.CreateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.dto.response.LeadDetailResponse;
import com.kiwipay.kiwipay_loan_backend.dto.response.LeadResponse;
import com.kiwipay.kiwipay_loan_backend.entity.LeadStatus;
import com.kiwipay.kiwipay_loan_backend.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * REST controller for Lead management.
 * Handles all lead-related HTTP requests.
 */
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leads", description = "Gestión de leads de préstamos médicos")
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
} 