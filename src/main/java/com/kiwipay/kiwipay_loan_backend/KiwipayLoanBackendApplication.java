package com.kiwipay.kiwipay_loan_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Aplicación principal de KiwiPay Loan Backend
 * Sistema de gestión de leads médicos para financiamiento
 * 
 * CONFIGURACIÓN DE SEGURIDAD:
 * - Se excluye UserDetailsServiceAutoConfiguration para evitar creación automática de usuarios
 * - Los usuarios se crean únicamente via endpoint /api/v1/auth/register
 * - No hay usuarios hardcodeados ni generados automáticamente
 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableAspectJAutoProxy
public class KiwipayLoanBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiwipayLoanBackendApplication.class, args);
	}

}
