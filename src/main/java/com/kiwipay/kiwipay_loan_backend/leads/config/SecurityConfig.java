package com.kiwipay.kiwipay_loan_backend.leads.config;

import com.kiwipay.kiwipay_loan_backend.leads.repository.UserRepository;
import com.kiwipay.kiwipay_loan_backend.leads.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * CONFIGURACIÓN DE SEGURIDAD MULTI-CAPA POR ENTORNO
 * 
 * CAPAS DE SEGURIDAD POR ENTORNO:
 * - DEV: Solo clave secreta de Swagger (SIN JWT)
 * - STAGING: Clave secreta de Swagger + JWT (doble capa)  
 * - PROD: Solo JWT (Swagger deshabilitado)
 * 
 * SwaggerSecurityAnnotations maneja la primera capa (clave secreta)
 * Este archivo maneja la segunda capa (JWT) según el perfil
 * 
 * NOTA: Dependencias inyectadas con @Lazy para evitar dependencias circulares
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Inyección lazy para evitar dependencias circulares
    @Autowired
    @Lazy
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    @Lazy
    private UserRepository userRepository;
    
    @Autowired
    @Lazy
    private JwtService jwtService;

    // =============================================================================
    // CONFIGURACIÓN DESARROLLO (DEV) - SIN JWT
    // =============================================================================
    
    /**
     * CONFIGURACIÓN DESARROLLO: SOLO CLAVE SECRETA
     * - SwaggerSecurityAnnotations maneja la clave secreta
     * - Una vez pasada la clave, TODO es público (sin JWT)
     */
    @Bean
    @Order(5)
    @Profile("dev")
    public SecurityFilterChain devSwaggerFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().sameOrigin())
            .build();
    }

    @Bean  
    @Order(10)
    @Profile("dev")
    public SecurityFilterChain devApiFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .contentTypeOptions(contentType -> {})
            )
            .authorizeHttpRequests(auth -> {
                // EN DEV: TODOS los endpoints de API son PÚBLICOS
                auth.requestMatchers("/api/**").permitAll();
                auth.anyRequest().permitAll();
            })
            .build();
    }

    @Bean
    @Order(15)
    @Profile("dev")
    public SecurityFilterChain devPublicResourcesFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/actuator/**", "/error", "/")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable())
            .build();
    }

    // =============================================================================
    // CONFIGURACIÓN STAGING - CLAVE SECRETA + JWT (DOBLE CAPA)
    // =============================================================================
    
    @Bean
    @Order(5)
    @Profile("staging")
    public SecurityFilterChain stagingSwaggerFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().sameOrigin())
            .build();
    }

    @Bean  
    @Order(10)
    @Profile("staging")
    public SecurityFilterChain stagingApiFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(contentType -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            .authorizeHttpRequests(auth -> {
                // Endpoints públicos de autenticación
                auth.requestMatchers("/api/v1/auth/**").permitAll();
                
                // Todos los demás endpoints de API requieren JWT
                auth.requestMatchers("/api/v1/leads/**").authenticated();
                auth.requestMatchers("/api/v1/clinics/**").authenticated();
                auth.requestMatchers("/api/v1/medical-specialties/**").authenticated();
                auth.requestMatchers("/api/v1/populate/**").authenticated();
                auth.anyRequest().authenticated();
            })
            .build();
    }

    @Bean
    @Order(15)
    @Profile("staging")
    public SecurityFilterChain stagingPublicResourcesFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/actuator/**", "/error", "/")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable())
            .build();
    }

    // =============================================================================
    // CONFIGURACIÓN PRODUCCIÓN - SOLO JWT (SIN SWAGGER)
    // =============================================================================
    
    @Bean  
    @Order(10)
    @Profile("prod")
    public SecurityFilterChain prodApiFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(contentType -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            .authorizeHttpRequests(auth -> {
                // Endpoints públicos de autenticación
                auth.requestMatchers("/api/v1/auth/**").permitAll();
                
                // Todos los demás endpoints de API requieren JWT
                auth.requestMatchers("/api/v1/leads/**").authenticated();
                auth.requestMatchers("/api/v1/clinics/**").authenticated();
                auth.requestMatchers("/api/v1/medical-specialties/**").authenticated();
                auth.requestMatchers("/api/v1/populate/**").authenticated();
                auth.anyRequest().authenticated();
            })
            .build();
    }

    @Bean
    @Order(15)
    @Profile("prod")
    public SecurityFilterChain prodPublicResourcesFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/actuator/**", "/error")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().deny())
            .build();
    }

    // =============================================================================
    // BEANS COMUNES PARA TODOS LOS PERFILES
    // =============================================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Bean
    @Profile({"staging", "prod"})
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService, userDetailsService());
    }
} 