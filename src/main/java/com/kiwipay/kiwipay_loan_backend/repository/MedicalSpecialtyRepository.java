package com.kiwipay.kiwipay_loan_backend.repository;

import com.kiwipay.kiwipay_loan_backend.entity.MedicalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MedicalSpecialty entity.
 */
@Repository
public interface MedicalSpecialtyRepository extends JpaRepository<MedicalSpecialty, Long> {
    
    /**
     * Find all active medical specialties.
     */
    List<MedicalSpecialty> findByActiveTrue();
    
    /**
     * Find medical specialties by category.
     */
    List<MedicalSpecialty> findByCategoryAndActiveTrue(String category);
    
    /**
     * Find medical specialty by name (case insensitive).
     */
    Optional<MedicalSpecialty> findByNameIgnoreCase(String name);
    
    /**
     * Find medical specialty by exact name.
     */
    Optional<MedicalSpecialty> findByName(String name);
} 