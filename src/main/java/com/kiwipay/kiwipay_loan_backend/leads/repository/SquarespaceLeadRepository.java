package com.kiwipay.kiwipay_loan_backend.leads.repository;

import com.kiwipay.kiwipay_loan_backend.leads.entity.LeadStatus;
import com.kiwipay.kiwipay_loan_backend.leads.entity.SquarespaceLead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar leads de Squarespace.
 */
@Repository
public interface SquarespaceLeadRepository extends JpaRepository<SquarespaceLead, Long> {

    /**
     * Verificar si existe un lead con el DNI dado.
     */
    boolean existsByDni(String dni);

    /**
     * Buscar leads por estado.
     */
    List<SquarespaceLead> findByStatus(LeadStatus status);

    /**
     * Buscar leads no procesados.
     */
    List<SquarespaceLead> findByProcessedFalse();

    /**
     * Buscar leads por sede.
     */
    List<SquarespaceLead> findBySedeIgnoreCase(String sede);

    /**
     * Buscar leads por rango de fechas.
     */
    @Query("SELECT s FROM SquarespaceLead s WHERE s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<SquarespaceLead> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Buscar leads con filtros múltiples.
     */
    @Query("SELECT s FROM SquarespaceLead s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:sede IS NULL OR LOWER(s.sede) LIKE LOWER(CONCAT('%', :sede, '%'))) AND " +
           "(:processed IS NULL OR s.processed = :processed) AND " +
           "(:startDate IS NULL OR s.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR s.createdAt <= :endDate) " +
           "ORDER BY s.createdAt DESC")
    Page<SquarespaceLead> findWithFilters(@Param("status") LeadStatus status,
                                         @Param("sede") String sede,
                                         @Param("processed") Boolean processed,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    /**
     * Contar leads por estado.
     */
    @Query("SELECT s.status, COUNT(s) FROM SquarespaceLead s GROUP BY s.status")
    List<Object[]> countByStatus();

    /**
     * Contar leads por sede.
     */
    @Query("SELECT s.sede, COUNT(s) FROM SquarespaceLead s GROUP BY s.sede ORDER BY COUNT(s) DESC")
    List<Object[]> countBySede();

    /**
     * Buscar leads creados hoy.
     */
    @Query("SELECT s FROM SquarespaceLead s WHERE DATE(s.createdAt) = CURRENT_DATE ORDER BY s.createdAt DESC")
    List<SquarespaceLead> findTodaysLeads();
} 