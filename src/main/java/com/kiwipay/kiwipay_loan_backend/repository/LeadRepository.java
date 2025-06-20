package com.kiwipay.kiwipay_loan_backend.repository;

import com.kiwipay.kiwipay_loan_backend.entity.Lead;
import com.kiwipay.kiwipay_loan_backend.entity.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for Lead entity.
 * Following Spring Data JPA best practices.
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    /**
     * Find lead by ID with clinic and medical specialty loaded.
     */
    @Query("SELECT l FROM Lead l " +
           "LEFT JOIN FETCH l.clinic " +
           "LEFT JOIN FETCH l.medicalSpecialty " +
           "WHERE l.id = :id")
    Optional<Lead> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all leads with filters and pagination.
     */
    @Query("SELECT l FROM Lead l " +
           "LEFT JOIN FETCH l.clinic c " +
           "LEFT JOIN FETCH l.medicalSpecialty ms " +
           "WHERE (:status IS NULL OR l.status = :status) " +
           "AND (:clinicId IS NULL OR c.id = :clinicId) " +
           "AND (:startDate IS NULL OR l.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR l.createdAt <= :endDate)")
    Page<Lead> findAllWithFilters(@Param("status") LeadStatus status,
                                  @Param("clinicId") Long clinicId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);

    /**
     * Check if a lead with the given DNI already exists.
     */
    boolean existsByDni(String dni);

    /**
     * Count leads by status.
     */
    long countByStatus(LeadStatus status);
} 