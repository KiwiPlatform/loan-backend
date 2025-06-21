package com.kiwipay.kiwipay_loan_backend.config;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CONFIGURACIÓN DE SEGURIDAD EMPRESARIAL PARA SWAGGER
 * 
 * SISTEMA DE AUTENTICACIÓN POR VARIABLES SECRETAS:
 * - PRODUCCIÓN: Swagger completamente DESHABILITADO
 * - STAGING: Requiere SWAGGER_STAGING_SECRET (8 horas de sesión)
 * - DESARROLLO: Requiere SWAGGER_DEV_SECRET (12 horas de sesión)
 * 
 * CARACTERÍSTICAS DE SEGURIDAD:
 * - Popup de autenticación con clave secreta mensual
 * - Bloqueo automático por 3 intentos fallidos (1 hora)
 * - Logging completo de eventos de seguridad
 * - Sesiones con timeout automático
 * - Validación segura con hashing SHA-256
 * - Protección contra ataques de fuerza bruta
 * 
 * @author alexander.castillo@kiwipay.pe
 * @version 2.0.0 - Seguridad Empresarial
 */
@Configuration
@EnableWebSecurity
public class SwaggerSecurityAnnotations {

    @Value("${SWAGGER_STAGING_SECRET:}")
    private String stagingSecret;

    @Value("${SWAGGER_DEV_SECRET:}")
    private String devSecret;

    // Control de acceso en memoria
    private final ConcurrentHashMap<String, AtomicInteger> failedAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> blockedIPs = new ConcurrentHashMap<>();

    // ==================== CONFIGURACIONES DE SEGURIDAD ====================

    /**
     * PRODUCCIÓN: Swagger completamente BLOQUEADO
     */
    @Bean
    @Order(1)
    @Profile("!dev & !staging")
    public SecurityFilterChain productionSwaggerBlock(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().denyAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            .build();
    }

    /**
     * STAGING: Autenticación con SWAGGER_STAGING_SECRET
     */
    @Bean
    @Order(2)
    @Profile("staging")
    public SecurityFilterChain stagingSwaggerAuth(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .addFilterBefore(new SwaggerAuthFilter("STAGING", stagingSecret, 8), 
                           UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            .build();
    }

    /**
     * DESARROLLO: Autenticación con SWAGGER_DEV_SECRET
     */
    @Bean
    @Order(3)
    @Profile("dev")
    public SecurityFilterChain developmentSwaggerAuth(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
            .addFilterBefore(new SwaggerAuthFilter("DESARROLLO", devSecret, 12), 
                           UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .build();
    }

    // ==================== FILTRO DE AUTENTICACIÓN ====================

    /**
     * FILTRO DE AUTENTICACIÓN PERSONALIZADO
     */
    public class SwaggerAuthFilter implements Filter {
        
        private final String environment;
        private final String secretKey;
        private final int sessionTimeoutHours;
        
        public SwaggerAuthFilter(String environment, String secretKey, int sessionTimeoutHours) {
            this.environment = environment;
            this.secretKey = secretKey;
            this.sessionTimeoutHours = sessionTimeoutHours;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                throws IOException, ServletException {
            
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String clientIP = getClientIP(req);

            // 1. Verificar si IP está bloqueada
            if (isIPBlocked(clientIP)) {
                logEvent("IP_BLOCKED", clientIP);
                sendBlockedPage(res);
                return;
            }

            // 2. Verificar sesión válida existente
            HttpSession session = req.getSession(false);
            if (session != null && isValidSession(session)) {
                chain.doFilter(request, response);
                return;
            }

            // 3. Procesar intento de autenticación
            String authKey = req.getHeader("X-Swagger-Auth");
            if (authKey != null) {
                if (validateSecret(authKey)) {
                    // Autenticación exitosa
                    createAuthenticatedSession(req);
                    failedAttempts.remove(clientIP);
                    logEvent("AUTH_SUCCESS", clientIP);
                    chain.doFilter(request, response);
                    return;
                } else {
                    // Clave incorrecta
                    handleFailedAuth(clientIP, res);
                    return;
                }
            }

            // 4. Mostrar página de autenticación
            sendAuthPage(res);
        }

        private String getClientIP(HttpServletRequest request) {
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null && !forwarded.isEmpty()) 
                ? forwarded.split(",")[0].trim() 
                : request.getRemoteAddr();
        }

        private boolean isIPBlocked(String ip) {
            LocalDateTime blockTime = blockedIPs.get(ip);
            if (blockTime != null) {
                if (LocalDateTime.now().isBefore(blockTime.plusHours(1))) {
                    return true;
                } else {
                    blockedIPs.remove(ip); // Limpiar bloqueo expirado
                }
            }
            return false;
        }

        private boolean isValidSession(HttpSession session) {
            Boolean authenticated = (Boolean) session.getAttribute("swagger_auth");
            String env = (String) session.getAttribute("swagger_env");
            LocalDateTime authTime = (LocalDateTime) session.getAttribute("auth_time");
            
            return authenticated != null && authenticated && 
                   environment.equals(env) && 
                   authTime != null && 
                   LocalDateTime.now().isBefore(authTime.plusHours(sessionTimeoutHours));
        }

        private boolean validateSecret(String provided) {
            if (secretKey == null || secretKey.trim().isEmpty()) {
                return false;
            }
            
            try {
                return MessageDigest.isEqual(
                    hashString(provided).getBytes(),
                    hashString(secretKey).getBytes()
                );
            } catch (Exception e) {
                logEvent("HASH_ERROR", "unknown");
                return false;
            }
        }

        private String hashString(String input) throws NoSuchAlgorithmException {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }

        private void createAuthenticatedSession(HttpServletRequest request) {
            HttpSession session = request.getSession(true);
            session.setAttribute("swagger_auth", true);
            session.setAttribute("swagger_env", environment);
            session.setAttribute("auth_time", LocalDateTime.now());
            session.setMaxInactiveInterval(sessionTimeoutHours * 3600);
        }

        private void handleFailedAuth(String ip, HttpServletResponse response) throws IOException {
            AtomicInteger attempts = failedAttempts.computeIfAbsent(ip, k -> new AtomicInteger(0));
            int count = attempts.incrementAndGet();
            
            logEvent("AUTH_FAILED_" + count, ip);
            
            if (count >= 3) {
                blockedIPs.put(ip, LocalDateTime.now());
                logEvent("IP_AUTO_BLOCKED", ip);
                sendBlockedPage(response);
            } else {
                sendErrorPage(response, 3 - count);
            }
        }

        private void sendAuthPage(HttpServletResponse response) throws IOException {
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            response.getWriter().write(String.format("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>KiwiPay API - Acceso Restringido</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: 'Segoe UI', system-ui, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            min-height: 100vh; display: flex; align-items: center; justify-content: center;
                        }
                        .container {
                            background: white; padding: 2.5rem; border-radius: 1rem;
                            box-shadow: 0 25px 50px rgba(0,0,0,0.15); width: 100%%; max-width: 420px;
                            text-align: center; animation: slideUp 0.6s ease-out;
                        }
                        @keyframes slideUp { from { opacity: 0; transform: translateY(30px); } }
                        .logo { font-size: 3rem; margin-bottom: 1rem; }
                        h1 { color: #2d3748; margin-bottom: 0.5rem; font-size: 1.5rem; }
                        .env-badge {
                            display: inline-block; background: #edf2f7; color: #4a5568;
                            padding: 0.5rem 1rem; border-radius: 1rem; font-size: 0.875rem;
                            font-weight: 600; margin-bottom: 2rem;
                        }
                        .warning {
                            background: #fef5e7; border: 1px solid #f6e05e; color: #744210;
                            padding: 1rem; border-radius: 0.5rem; margin-bottom: 1.5rem;
                            font-size: 0.875rem; text-align: left;
                        }
                        .input-group { margin-bottom: 1.5rem; text-align: left; }
                        label { display: block; margin-bottom: 0.5rem; color: #4a5568; font-weight: 600; }
                        input {
                            width: 100%%; padding: 0.75rem; border: 2px solid #e2e8f0;
                            border-radius: 0.5rem; font-size: 1rem; transition: all 0.2s;
                        }
                        input:focus { outline: none; border-color: #667eea; box-shadow: 0 0 0 3px rgba(102,126,234,0.1); }
                        .btn {
                            width: 100%%; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white; border: none; padding: 0.875rem; border-radius: 0.5rem;
                            font-size: 1rem; font-weight: 600; cursor: pointer; transition: transform 0.2s;
                        }
                        .btn:hover { transform: translateY(-2px); }
                        .footer { margin-top: 2rem; font-size: 0.75rem; color: #a0aec0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="logo">[LOCK]</div>
                        <h1>Acceso Restringido</h1>
                        <div class="env-badge">Entorno: %s</div>
                        
                        <div class="warning">
                            <strong>Autenticación Requerida</strong><br>
                            Este entorno requiere clave secreta mensual. Todos los accesos son auditados.
                        </div>
                        
                        <form id="authForm">
                            <div class="input-group">
                                <label for="secret">Clave de Acceso Mensual:</label>
                                <input type="password" id="secret" placeholder="Ingrese la clave secreta" required>
                            </div>
                            <button type="submit" class="btn">Acceder a Swagger</button>
                        </form>
                        
                        <div class="footer">
                            KiwiPay API - Seguridad Empresarial<br>
                            Desarrollador: alexander.castillo@kiwipay.pe
                        </div>
                    </div>
                    
                    <script>
                        document.getElementById('authForm').onsubmit = function(e) {
                            e.preventDefault();
                            const secret = document.getElementById('secret').value;
                            if (!secret) { alert('Ingrese la clave de acceso'); return; }
                            
                            fetch(location.href, {
                                method: 'GET',
                                headers: { 'X-Swagger-Auth': secret }
                            }).then(() => location.reload());
                        };
                        document.getElementById('secret').focus();
                    </script>
                </body>
                </html>
                """, environment));
        }

        private void sendErrorPage(HttpServletResponse response, int remaining) throws IOException {
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            response.getWriter().write(String.format("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>Clave Incorrecta - KiwiPay API</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', system-ui, sans-serif;
                            background: linear-gradient(135deg, #ff6b6b 0%%, #ee5a24 100%%);
                            min-height: 100vh; display: flex; align-items: center; justify-content: center;
                        }
                        .container {
                            background: white; padding: 2.5rem; border-radius: 1rem;
                            box-shadow: 0 25px 50px rgba(0,0,0,0.15); max-width: 420px; text-align: center;
                        }
                        .icon { font-size: 3rem; margin-bottom: 1rem; }
                        h1 { color: #e53e3e; margin-bottom: 1rem; }
                        .message {
                            background: #fed7d7; border: 1px solid #fc8181; color: #c53030;
                            padding: 1rem; border-radius: 0.5rem; margin-bottom: 1.5rem;
                        }
                        .btn {
                            background: #3182ce; color: white; padding: 0.75rem 1.5rem;
                            border: none; border-radius: 0.5rem; text-decoration: none;
                            display: inline-block; font-weight: 600;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="icon">[X]</div>
                        <h1>Clave Incorrecta</h1>
                        <div class="message">
                            La clave ingresada es incorrecta.<br>
                            <strong>Intentos restantes: %d</strong><br>
                            Después de 3 intentos fallidos, su IP será bloqueada por 1 hora.
                        </div>
                        <a href="javascript:history.back()" class="btn">Reintentar</a>
                    </div>
                </body>
                </html>
                """, remaining));
        }

        private void sendBlockedPage(HttpServletResponse response) throws IOException {
            response.setContentType("text/html; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            
            response.getWriter().write("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <title>IP Bloqueada - KiwiPay API</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', system-ui, sans-serif;
                            background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
                            min-height: 100vh; display: flex; align-items: center; justify-content: center; color: white;
                        }
                        .container {
                            background: rgba(255,255,255,0.1); padding: 2.5rem; border-radius: 1rem;
                            backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.2);
                            max-width: 500px; text-align: center;
                        }
                        .icon { font-size: 4rem; margin-bottom: 1rem; }
                        h1 { color: #fc8181; margin-bottom: 1rem; }
                        .message {
                            background: rgba(252,129,129,0.2); padding: 1.5rem; border-radius: 0.5rem;
                            border: 1px solid rgba(252,129,129,0.3); margin-bottom: 1.5rem;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="icon">[BLOCKED]</div>
                        <h1>Acceso Bloqueado</h1>
                        <div class="message">
                            Su IP ha sido bloqueada temporalmente por múltiples intentos fallidos.
                            <br><br>
                            <strong>Duración del bloqueo: 1 hora</strong>
                            <br><br>
                            Para acceso inmediato, contacte al administrador.
                        </div>
                        <p><strong>Contacto:</strong> alexander.castillo@kiwipay.pe</p>
                    </div>
                </body>
                </html>
                """);
        }

        private void logEvent(String event, String ip) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            System.out.printf("[%s] SWAGGER_SECURITY: %s | ENV=%s | IP=%s%n", 
                            timestamp, event, environment, ip);
        }
    }

    // ==================== ANOTACIONES DE SEGURIDAD ====================

    /**
     * Requiere autenticación JWT Bearer Token
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirement(name = "Bearer Authentication")
    public @interface RequireJwtAuth {
    }

    /**
     * Requiere OAuth2 con scopes específicos
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirement(name = "OAuth2 Authorization Code")
    public @interface RequireOAuth2 {
    }

    /**
     * Múltiples opciones de autenticación
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements({
        @SecurityRequirement(name = "Bearer Authentication"),
        @SecurityRequirement(name = "OAuth2 Authorization Code")
    })
    public @interface RequireAuthAny {
    }

    /**
     * Requiere autenticación de administrador
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements({
        @SecurityRequirement(name = "Bearer Authentication"),
        @SecurityRequirement(name = "OAuth2 Authorization Code")
    })
    public @interface RequireAdminAuth {
    }

    /**
     * Solo para desarrollo - autenticación flexible
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @SecurityRequirements({
        @SecurityRequirement(name = "Bearer Authentication"),
        @SecurityRequirement(name = "Basic Authentication")
    })
    public @interface DevOnlyAuth {
    }
} 