package com.kiwipay.kiwipay_loan_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for API documentation.
 * Configures Swagger UI with application details and tag ordering.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KiwiPay Loan Backend")
                        .version("1.0.0")
                        .description("API for managing medical loan leads for KiwiPay Peru")
                        .description("by Alexander Jair Castillo ")
                        .contact(new Contact()
                                .name("KiwiPay Development Team")
                                .email("dev@kiwipay.pe")
                                .url("https://kiwipay.pe"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://kiwipay.pe/terms")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.kiwipay.pe")
                                .description("Production Server")
                ));
    }
}