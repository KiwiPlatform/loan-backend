package com.kiwipay.kiwipay_loan_backend.service;

import com.kiwipay.kiwipay_loan_backend.dto.request.CreateLeadRequest;
import com.kiwipay.kiwipay_loan_backend.dto.response.LeadDetailResponse;
import com.kiwipay.kiwipay_loan_backend.dto.response.LeadResponse;
import com.kiwipay.kiwipay_loan_backend.entity.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Service interface for Lead management.
 * Defines the business operations for leads.
 */
public interface LeadService {

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
} 