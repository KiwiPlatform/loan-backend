package com.kiwipay.kiwipay_loan_backend.leads.service;

import com.kiwipay.kiwipay_loan_backend.leads.dto.request.SquarespaceLeadRequest;
import com.kiwipay.kiwipay_loan_backend.leads.dto.response.SquarespaceLeadResponse;
import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Servicio completo para gestionar leads de Squarespace.
 */
public interface SquarespaceService {
    
    /**
     * Procesa un lead recibido desde Squarespace.
     * 
     * @param request datos del formulario de Squarespace
     * @return ID del lead creado
     */
    Long processSquarespaceLead(SquarespaceLeadRequest request);
    
    /**
     * Obtiene leads de Squarespace con filtros.
     * 
     * @param status estado del lead
     * @param sede sede del lead
     * @param processed si ya fue procesado
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @param pageable paginación
     * @return página de leads
     */
    Page<SquarespaceLeadResponse> getSquarespaceLeads(LeadStatus status,
                                                     String sede,
                                                     Boolean processed,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate,
                                                     Pageable pageable);
    
    /**
     * Obtiene leads de hoy.
     * 
     * @return lista de leads del día
     */
    List<SquarespaceLeadResponse> getTodaysLeads();
    
    /**
     * Obtiene estadísticas de leads.
     * 
     * @return mapa con estadísticas
     */
    Map<String, Object> getLeadStatistics();
    
    /**
     * Marca un lead como procesado.
     * 
     * @param id ID del lead
     * @param notes notas adicionales
     * @return lead actualizado
     */
    SquarespaceLeadResponse markAsProcessed(Long id, String notes);
} 