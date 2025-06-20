package com.kiwipay.kiwipay_loan_backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Implements RFC 7807 Problem Details for HTTP APIs.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.error("Business rule violation: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage()
        );
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed"
        );
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getDescription(false));
        problemDetail.setProperty("errors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please try again later."
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("path", request.getDescription(false));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
} 