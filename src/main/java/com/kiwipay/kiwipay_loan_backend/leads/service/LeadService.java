package com.kiwipay.kiwipay_loan_backend.leads.service;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.CreateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.request.UpdateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.request.SquarespaceBackendRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.LeadDetailResponse;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.LeadResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Lead management.
 * Defines the business operations for leads.
 */
public interface LeadService {

    /**
     * Create a new lead from Squarespace API with raw data.
     * Handles all necessary conversions internally.
     * 
     * @param request the Squarespace backend request with raw data
     * @return the created lead details
     */
    LeadDetailResponse createLeadFromSquarespace(SquarespaceBackendRequest request);

    /**
     * Create a new lead from the form data.
     * 
     * @param request the lead creation request
     * @return the created lead details
     */
    LeadDetailResponse createLead(CreateLeadRequest request);

    /**
     * Get paginated list of leads with optional filters.
     * 
     * @param status optional status filter
     * @param clinicId optional clinic filter
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @param pageable pagination information
     * @return page of lead responses
     */
    Page<LeadResponse> getLeads(LeadStatus status, 
                                Long clinicId, 
                                LocalDateTime startDate, 
                                LocalDateTime endDate, 
                                Pageable pageable);

    /**
     * Get detailed information of a specific lead.
     * 
     * @param id the lead ID
     * @return the lead details
     */
    LeadDetailResponse getLeadById(Long id);

    /**
     * Update the status of a lead.
     * 
     * @param id the lead ID
     * @param status the new status
     * @return the updated lead details
     */
    LeadDetailResponse updateLeadStatus(Long id, LeadStatus status);

    /**
     * Update a complete lead with all its information.
     * 
     * @param id the lead ID
     * @param request the update request with new data
     * @return the updated lead details
     */
    LeadDetailResponse updateLead(Long id, UpdateLeadRequest request);

    /**
     * Get all leads without pagination (for admin purposes).
     * Use with caution in production with large datasets.
     * 
     * @return list of all leads
     */
    List<LeadResponse> getAllLeads();

    /**
     * Get all leads with optional filters but without pagination.
     * 
     * @param status optional status filter
     * @param clinicId optional clinic filter
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return filtered list of leads
     */
    List<LeadResponse> getAllLeadsFiltered(LeadStatus status, 
                                          Long clinicId, 
                                          LocalDateTime startDate, 
                                          LocalDateTime endDate);
} 