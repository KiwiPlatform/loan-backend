package com.kiwipay.kiwipay_loan_backend.leads.service.impl;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.SquarespaceLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.SquarespaceLeadResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import com.kiwipay.kiwipay_loan_backend.leads.entity.SquarespaceLead;
import com.kiwipay.kiwipay_loan_backend.leads.exception.ResourceNotFoundException;
import com.kiwipay.kiwipay_loan_backend.leads.repository.SquarespaceLeadRepository;
import com.kiwipay.kiwipay_loan_backend.leads.service.SquarespaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación completa del servicio Squarespace.
 * Gestiona leads en tabla específica con funcionalidades completas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SquarespaceServiceImpl implements SquarespaceService {

    private final SquarespaceLeadRepository squarespaceLeadRepository;

    @Override
    public Long processSquarespaceLead(SquarespaceLeadRequest request) {
        log.debug("Procesando lead de Squarespace con DNI: {}", request.getDni());
        
        // Crear lead específico de Squarespace
        SquarespaceLead lead = SquarespaceLead.builder()
                .receptionistName(request.getReceptionistName())
                .sede(request.getSede()) // Guardamos tal como viene del formulario
                .clientName(request.getClientName())
                .dni(request.getDni())
                .monthlyIncome(request.getMonthlyIncome())
                .treatmentCost(request.getTreatmentCost())
                .phone(request.getPhone())
                .status(LeadStatus.NUEVO)
                .processed(false) // Marcamos como no procesado
                .build();
        
        // Guardar en tabla específica de Squarespace
        SquarespaceLead savedLead = squarespaceLeadRepository.save(lead);
        
        log.info("Lead de Squarespace guardado en tabla específica - ID: {}, DNI: {}, Sede: {}", 
                savedLead.getId(), savedLead.getDni(), savedLead.getSede());
        
        return savedLead.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SquarespaceLeadResponse> getSquarespaceLeads(LeadStatus status,
                                                           String sede,
                                                           Boolean processed,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate,
                                                           Pageable pageable) {
        log.debug("Consultando leads de Squarespace con filtros");
        
        Page<SquarespaceLead> leads = squarespaceLeadRepository.findWithFilters(
            status, sede, processed, startDate, endDate, pageable);
        
        return leads.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SquarespaceLeadResponse> getTodaysLeads() {
        log.debug("Obteniendo leads de hoy");
        
        List<SquarespaceLead> leads = squarespaceLeadRepository.findTodaysLeads();
        return leads.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLeadStatistics() {
        log.debug("Generando estadísticas de leads");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Total de leads
        long totalLeads = squarespaceLeadRepository.count();
        stats.put("totalLeads", totalLeads);
        
        // Leads no procesados
        long unprocessedLeads = squarespaceLeadRepository.findByProcessedFalse().size();
        stats.put("unprocessedLeads", unprocessedLeads);
        
        // Leads de hoy
        long todaysLeads = squarespaceLeadRepository.findTodaysLeads().size();
        stats.put("todaysLeads", todaysLeads);
        
        // Estadísticas por estado
        List<Object[]> statusStats = squarespaceLeadRepository.countByStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusCounts.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("byStatus", statusCounts);
        
        // Estadísticas por sede
        List<Object[]> sedeStats = squarespaceLeadRepository.countBySede();
        Map<String, Long> sedeCounts = new HashMap<>();
        for (Object[] stat : sedeStats) {
            sedeCounts.put(stat[0] != null ? stat[0].toString() : "Sin sede", (Long) stat[1]);
        }
        stats.put("bySede", sedeCounts);
        
        return stats;
    }

    @Override
    public SquarespaceLeadResponse markAsProcessed(Long id, String notes) {
        log.debug("Marcando lead {} como procesado", id);
        
        SquarespaceLead lead = squarespaceLeadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con ID: " + id));
        
        lead.setProcessed(true);
        if (notes != null && !notes.trim().isEmpty()) {
            lead.setNotes(notes.trim());
        }
        
        SquarespaceLead updatedLead = squarespaceLeadRepository.save(lead);
        log.info("Lead {} marcado como procesado", id);
        
        return mapToResponse(updatedLead);
    }

    // Método privado para mapear entidad a DTO
    private SquarespaceLeadResponse mapToResponse(SquarespaceLead lead) {
        return SquarespaceLeadResponse.builder()
                .id(lead.getId())
                .receptionistName(lead.getReceptionistName())
                .sede(lead.getSede())
                .clientName(lead.getClientName())
                .dni(lead.getDni())
                .monthlyIncome(lead.getMonthlyIncome())
                .treatmentCost(lead.getTreatmentCost())
                .phone(lead.getPhone())
                .status(lead.getStatus())
                .processed(lead.getProcessed())
                .notes(lead.getNotes())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
} 