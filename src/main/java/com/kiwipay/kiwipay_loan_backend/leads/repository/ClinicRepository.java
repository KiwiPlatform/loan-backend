package com.kiwipay.kiwipay_loan_backend.leads.repository;

import com.kiwipay.kiwipay_loan_backend.leads.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Clinic entity.
 */
@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    
    /**
     * Find all active clinics.
     */
    List<Clinic> findByActiveTrue();
    
    /**
     * Find clinic by name.
     */
    boolean existsByName(String name);
    
    /**
     * Find clinic by name (case insensitive).
     */
    Optional<Clinic> findByNameIgnoreCase(String name);
    
    /**
     * Find clinic by exact name.
     */
    Optional<Clinic> findByName(String name);
} 