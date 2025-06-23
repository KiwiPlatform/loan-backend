package com.kiwipay.kiwipay_loan_backend.leads.service.impl;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.CreateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.request.UpdateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.LeadDetailResponse;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.LeadResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.Clinic;
import com.kiwipay.kiwipay_loan_backend.leads.entity.Lead;
import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import com.kiwipay.kiwipay_loan_backend.leads.entity.MedicalSpecialty;
import com.kiwipay.kiwipay_loan_backend.leads.exception.ResourceNotFoundException;
import com.kiwipay.kiwipay_loan_backend.leads.exception.BusinessException;
import com.kiwipay.kiwipay_loan_backend.leads.repository.ClinicRepository;
import com.kiwipay.kiwipay_loan_backend.leads.repository.LeadRepository;
import com.kiwipay.kiwipay_loan_backend.leads.repository.MedicalSpecialtyRepository;
import com.kiwipay.kiwipay_loan_backend.leads.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of LeadService.
 * Contains the business logic for lead management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final ClinicRepository clinicRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;

    @Override
    @Transactional
    public LeadDetailResponse createLead(CreateLeadRequest request) {
        log.debug("Creating new lead with DNI: {}", request.getDni());

        // Validate that DNI doesn't already exist
        if (leadRepository.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un lead con el DNI: " + request.getDni());
        }

        // Fetch and validate clinic
        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clínica no encontrada con ID: " + request.getClinicId()));

        if (!clinic.getActive()) {
            throw new BusinessException("La clínica seleccionada no está activa");
        }

        // Fetch and validate medical specialty
        MedicalSpecialty medicalSpecialty = medicalSpecialtyRepository.findById(request.getMedicalSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad médica no encontrada con ID: " + request.getMedicalSpecialtyId()));

        if (!medicalSpecialty.getActive()) {
            throw new BusinessException("La especialidad médica seleccionada no está activa");
        }

        // Create and save lead manually
        Lead lead = new Lead();
        lead.setReceptionistName(request.getReceptionistName());
        lead.setClientName(request.getClientName());
        lead.setDni(request.getDni());
        lead.setMonthlyIncome(request.getMonthlyIncome());
        lead.setTreatmentCost(request.getTreatmentCost());
        lead.setPhone(request.getPhone());
        lead.setEmail(request.getEmail());
        lead.setClinic(clinic);
        lead.setMedicalSpecialty(medicalSpecialty);
        lead.setStatus(LeadStatus.NUEVO);
        lead.setOrigin("WEB");

        Lead savedLead = leadRepository.save(lead);
        log.info("Lead created successfully with ID: {}", savedLead.getId());

        return mapToDetailResponse(savedLead);
    }

    @Override
    public Page<LeadResponse> getLeads(LeadStatus status, 
                                      Long clinicId, 
                                      LocalDateTime startDate, 
                                      LocalDateTime endDate, 
                                      Pageable pageable) {
        log.debug("Fetching leads with filters - status: {}, clinicId: {}, startDate: {}, endDate: {}", 
                  status, clinicId, startDate, endDate);

        Page<Lead> leads = leadRepository.findAllWithFilters(status, clinicId, startDate, endDate, pageable);
        
        return leads.map(this::mapToResponse);
    }

    @Override
    public LeadDetailResponse getLeadById(Long id) {
        log.debug("Fetching lead with ID: {}", id);

        Lead lead = leadRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + id));

        return mapToDetailResponse(lead);
    }

    @Override
    @Transactional
    public LeadDetailResponse updateLeadStatus(Long id, LeadStatus status) {
        log.debug("Updating lead {} status to: {}", id, status);

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + id));

        // Validate status transition
        validateStatusTransition(lead.getStatus(), status);

        lead.setStatus(status);
        Lead updatedLead = leadRepository.save(lead);
        
        log.info("Lead {} status updated to: {}", id, status);

        return mapToDetailResponse(updatedLead);
    }

    @Override
    @Transactional
    public LeadDetailResponse updateLead(Long id, UpdateLeadRequest request) {
        log.debug("Updating lead with ID: {}", id);

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + id));

        // Update fields only if they are provided in the request
        if (request.getReceptionistName() != null) {
            lead.setReceptionistName(request.getReceptionistName());
        }
        if (request.getClientName() != null) {
            lead.setClientName(request.getClientName());
        }
        if (request.getDni() != null) {
            // Check if DNI is already used by another lead
            if (leadRepository.existsByDniAndIdNot(request.getDni(), id)) {
                throw new BusinessException("Ya existe otro lead con el DNI: " + request.getDni());
            }
            lead.setDni(request.getDni());
        }
        if (request.getMonthlyIncome() != null) {
            lead.setMonthlyIncome(request.getMonthlyIncome());
        }
        if (request.getTreatmentCost() != null) {
            lead.setTreatmentCost(request.getTreatmentCost());
        }
        if (request.getPhone() != null) {
            lead.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            lead.setEmail(request.getEmail());
        }
        if (request.getOrigin() != null) {
            lead.setOrigin(request.getOrigin());
        }

        // Update clinic if provided
        if (request.getClinicId() != null) {
            Clinic clinic = clinicRepository.findById(request.getClinicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica no encontrada con ID: " + request.getClinicId()));
            if (!clinic.getActive()) {
                throw new BusinessException("La clínica seleccionada no está activa");
            }
            lead.setClinic(clinic);
        }

        // Update medical specialty if provided
        if (request.getMedicalSpecialtyId() != null) {
            MedicalSpecialty medicalSpecialty = medicalSpecialtyRepository.findById(request.getMedicalSpecialtyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Especialidad médica no encontrada con ID: " + request.getMedicalSpecialtyId()));
            if (!medicalSpecialty.getActive()) {
                throw new BusinessException("La especialidad médica seleccionada no está activa");
            }
            lead.setMedicalSpecialty(medicalSpecialty);
        }

        // Update status if provided
        if (request.getStatus() != null) {
            try {
                LeadStatus newStatus = LeadStatus.valueOf(request.getStatus().toUpperCase());
                validateStatusTransition(lead.getStatus(), newStatus);
                lead.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estado inválido: " + request.getStatus());
            }
        }

        Lead updatedLead = leadRepository.save(lead);
        log.info("Lead {} updated successfully", id);

        return mapToDetailResponse(updatedLead);
    }

    @Override
    public List<LeadResponse> getAllLeads() {
        log.debug("Fetching all leads without pagination");
        
        List<Lead> leads = leadRepository.findAllOrderByCreatedAtDesc();
        return leads.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<LeadResponse> getAllLeadsFiltered(LeadStatus status, 
                                                 Long clinicId, 
                                                 LocalDateTime startDate, 
                                                 LocalDateTime endDate) {
        log.debug("Fetching all leads with filters - status: {}, clinicId: {}, startDate: {}, endDate: {}", 
                  status, clinicId, startDate, endDate);

        List<Lead> leads = leadRepository.findAllWithFiltersNoPagination(status, clinicId, startDate, endDate);
        return leads.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Validates if the status transition is allowed.
     */
    private void validateStatusTransition(LeadStatus currentStatus, LeadStatus newStatus) {
        // Business rule: Cannot move from RECHAZADO or DESEMBOLSADO to any other status
        if ((currentStatus == LeadStatus.RECHAZADO || currentStatus == LeadStatus.DESEMBOLSADO) 
            && currentStatus != newStatus) {
            throw new BusinessException(
                String.format("No se puede cambiar el estado de %s a %s", 
                             currentStatus.getDisplayName(), 
                             newStatus.getDisplayName())
            );
        }
    }

    // Manual mapping methods
    private LeadResponse mapToResponse(Lead lead) {
        LeadResponse response = new LeadResponse();
        response.setId(lead.getId());
        response.setClientName(lead.getClientName());
        response.setDni(lead.getDni());
        response.setClinicName(lead.getClinic().getName());
        response.setMedicalSpecialtyName(lead.getMedicalSpecialty().getName());
        response.setTreatmentCost(lead.getTreatmentCost());
        response.setPhone(lead.getPhone());
        response.setStatus(lead.getStatus());
        response.setCreatedAt(lead.getCreatedAt());
        return response;
    }

    private LeadDetailResponse mapToDetailResponse(Lead lead) {
        LeadDetailResponse response = new LeadDetailResponse();
        response.setId(lead.getId());
        response.setReceptionistName(lead.getReceptionistName());
        response.setClientName(lead.getClientName());
        response.setDni(lead.getDni());
        response.setPhone(lead.getPhone());
        response.setEmail(lead.getEmail());
        response.setMonthlyIncome(lead.getMonthlyIncome());
        response.setTreatmentCost(lead.getTreatmentCost());
        response.setClinicId(lead.getClinic().getId());
        response.setClinicName(lead.getClinic().getName());
        response.setMedicalSpecialtyId(lead.getMedicalSpecialty().getId());
        response.setMedicalSpecialtyName(lead.getMedicalSpecialty().getName());
        response.setStatus(lead.getStatus());
        response.setOrigin(lead.getOrigin());
        response.setCreatedAt(lead.getCreatedAt());
        response.setUpdatedAt(lead.getUpdatedAt());
        return response;
    }
} 