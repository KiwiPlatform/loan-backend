package com.kiwipay.kiwipay_loan_backend.leads.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuración personalizada para el orden de tags en Swagger UI.
 * Asegura que los endpoints de negocio aparezcan antes que Actuator.
 */
@Configuration
public class SwaggerOrderConfig {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Definir el orden deseado de los tags
            List<Tag> orderedTags = new ArrayList<>();
            
            // 1. Endpoints de negocio primero
            orderedTags.add(new Tag()
                    .name("Leads")
                    .description("Gestión de leads de préstamos médicos"));
            
            orderedTags.add(new Tag()
                    .name("Clinics")
                    .description("Gestión de clínicas y centros médicos"));
            
            orderedTags.add(new Tag()
                    .name("Medical Specialties")
                    .description("Gestión de especialidades médicas"));
            
            // 2. Actuator al final
            orderedTags.add(new Tag()
                    .name("Actuator")
                    .description("Endpoints de monitoreo y gestión de la aplicación"));
            
            // Aplicar el orden
            openApi.setTags(orderedTags);
        };
    }
} 