package com.kiwipay.kiwipay_loan_backend.leads.service;

import com.kiwipay.kiwipay_loan_backend.leads.entity.Clinic;
import com.kiwipay.kiwipay_loan_backend.leads.entity.MedicalSpecialty;
import com.kiwipay.kiwipay_loan_backend.leads.repository.ClinicRepository;
import com.kiwipay.kiwipay_loan_backend.leads.repository.MedicalSpecialtyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Servicio para poblar la base de datos desde el archivo Excel seed.xlsx
 * Implementa el mismo flujo que el sistema TypeScript original
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelPopulateService {
    
    private final ClinicRepository clinicRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    
    // Constantes para el procesamiento del Excel
    private static final String EXCEL_FILE_PATH = "static/seed.xlsx";
    private static final int HEALTH_CENTERS_SHEET_INDEX = 1; // Hoja 2 (índice 1)
    private static final int SPECIALTIES_SHEET_INDEX = 2;    // Hoja 3 (índice 2)
    private static final int DATA_START_ROW = 2;             // Fila 3 (índice 2)
    
    /**
     * Método principal para poblar la base de datos desde Excel
     */
    @Transactional
    public void populateFromExcel() {
        log.info("Iniciando proceso de populate desde Excel: {}", EXCEL_FILE_PATH);
        
        try {
            // 1. Leer archivo Excel
            Workbook workbook = loadExcelFile();
            
            // 2. Procesar centros de salud (Hoja 2)
            int insertedClinics = processHealthCenters(workbook);
            
            // 3. Procesar especialidades (Hoja 3)
            int insertedSpecialties = processSpecialties(workbook);
            
            // 4. Mostrar resumen
            showSummary(insertedClinics, insertedSpecialties);
            
            workbook.close();
            
        } catch (Exception e) {
            log.error("Error durante el proceso de populate: {}", e.getMessage(), e);
            throw new RuntimeException("Error poblando la base de datos desde Excel", e);
        }
    }
    
    /**
     * Carga el archivo Excel desde el classpath
     */
    private Workbook loadExcelFile() throws IOException {
        log.info("📂 Cargando archivo Excel: {}", EXCEL_FILE_PATH);
        
        ClassPathResource resource = new ClassPathResource(EXCEL_FILE_PATH);
        if (!resource.exists()) {
            throw new RuntimeException("Archivo Excel no encontrado: " + EXCEL_FILE_PATH);
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            log.info("Excel cargado exitosamente. Hojas encontradas: {}", workbook.getNumberOfSheets());
            
            // Mostrar nombres de las hojas
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                log.info("   📋 Hoja {}: {}", i + 1, workbook.getSheetName(i));
            }
            
            return workbook;
        }
    }
    
    /**
     * Procesa la hoja de centros de salud (Hoja 2)
     */
    private int processHealthCenters(Workbook workbook) {
        log.info("🏥 Procesando centros de salud...");
        
        Sheet sheet = workbook.getSheetAt(HEALTH_CENTERS_SHEET_INDEX);
        int insertedCount = 0;
        
        for (int rowIndex = DATA_START_ROW; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isEmptyRow(row)) {
                continue;
            }
            
            try {
                // Columnas B-I (índices 1-8): name, sede, país, región, provincia, distrito, teléfono, dirección
                String name = getCellValueAsString(row.getCell(1));
                String address = getCellValueAsString(row.getCell(8));
                
                if (isBlank(name)) {
                    continue;
                }
                
                // Verificar si ya existe
                Optional<Clinic> existingClinic = clinicRepository.findByName(name.trim());
                if (existingClinic.isPresent()) {
                    log.debug("Clínica ya existe: {}", name);
                    continue;
                }
                
                // Crear nueva clínica
                Clinic clinic = Clinic.builder()
                        .name(capitalizeWords(name.trim()))
                        .address(capitalizeWords(address.trim()))
                        .active(true)
                        .build();
                
                Clinic savedClinic = clinicRepository.save(clinic);
                insertedCount++;
                
                log.info("Clínica insertada: {} (ID: {})", savedClinic.getName(), savedClinic.getId());
                
            } catch (Exception e) {
                log.warn("Error procesando fila {} de centros de salud: {}", rowIndex + 1, e.getMessage());
            }
        }
        
        log.info("Procesados {} centros de salud", insertedCount);
        return insertedCount;
    }
    
    /**
     * Procesa la hoja de especialidades (Hoja 3)
     */
    private int processSpecialties(Workbook workbook) {
        log.info("🩺 Procesando especialidades...");
        
        Sheet sheet = workbook.getSheetAt(SPECIALTIES_SHEET_INDEX);
        int insertedCount = 0;
        
        for (int rowIndex = DATA_START_ROW; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null || isEmptyRow(row)) {
                continue;
            }
            
            try {
                // Columnas B-D (índices 1-3): selector, categoría, especialidad
                String category = getCellValueAsString(row.getCell(2));
                String specialty = getCellValueAsString(row.getCell(3));
                
                if (isBlank(category) || isBlank(specialty)) {
                    continue;
                }
                
                // Verificar si ya existe
                Optional<MedicalSpecialty> existingSpecialty = 
                        medicalSpecialtyRepository.findByName(specialty.trim());
                if (existingSpecialty.isPresent()) {
                    log.debug("Especialidad ya existe: {}", specialty);
                    continue;
                }
                
                // Crear nueva especialidad
                MedicalSpecialty medicalSpecialty = MedicalSpecialty.builder()
                        .name(capitalizeWords(specialty.trim()))
                        .category(capitalizeWords(category.trim()))
                        .active(true)
                        .build();
                
                MedicalSpecialty savedSpecialty = medicalSpecialtyRepository.save(medicalSpecialty);
                insertedCount++;
                
                log.info("Especialidad insertada: {} - {} (ID: {})", 
                        savedSpecialty.getCategory(), savedSpecialty.getName(), savedSpecialty.getId());
                
            } catch (Exception e) {
                log.warn("Error procesando fila {} de especialidades: {}", rowIndex + 1, e.getMessage());
            }
        }
        
        log.info("Procesadas {} especialidades", insertedCount);
        return insertedCount;
    }
    
    /**
     * Muestra un resumen del proceso
     */
    private void showSummary(int insertedClinics, int insertedSpecialties) {
        log.info("\nRESUMEN DEL PROCESO DE POPULATE:");
        log.info("Clínicas insertadas: {}", insertedClinics);
        log.info("Especialidades insertadas: {}", insertedSpecialties);
        
        // Mostrar totales en BD
        long totalClinics = clinicRepository.count();
        long totalSpecialties = medicalSpecialtyRepository.count();
        
        log.info("🏥 Total clínicas en BD: {}", totalClinics);
        log.info("🩺 Total especialidades en BD: {}", totalSpecialties);
        log.info("🎉 PROCESO COMPLETADO EXITOSAMENTE!");
    }
    
    // Métodos utilitarios
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convertir número a string sin notación científica
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !isBlank(getCellValueAsString(cell))) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private String capitalizeWords(String str) {
        if (isBlank(str)) {
            return str;
        }
        
        String[] words = str.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
} 