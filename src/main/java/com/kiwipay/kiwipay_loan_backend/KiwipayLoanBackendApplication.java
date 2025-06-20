package com.kiwipay.kiwipay_loan_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for KiwiPay Loan Backend.
 * This application manages medical loan leads for KiwiPay Peru.
 * 
 * @author KiwiPay Development Team
 * @version 1.0.0
 */
@SpringBootApplication
public class KiwipayLoanBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiwipayLoanBackendApplication.class, args);
	}

}
