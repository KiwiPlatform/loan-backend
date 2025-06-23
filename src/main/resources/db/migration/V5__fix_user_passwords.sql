-- =============================================================================
-- MIGRACIÓN V5: PREPARACIÓN PARA USUARIOS DINÁMICOS
-- Descripción: Limpia usuarios existentes y prepara para creación dinámica
-- Autor: Sistema de Autenticación KiwiPay
-- Fecha: 2024-12-21
-- =============================================================================

-- Eliminar cualquier usuario existente por seguridad
-- Esto garantiza un sistema limpio para usuarios dinámicos
DELETE FROM users WHERE username IN ('admin', 'testuser');

-- NOTA IMPORTANTE:
-- Los usuarios ahora se crearán exclusivamente usando el endpoint /api/v1/auth/register
-- Esto garantiza:
-- 1. Hashes BCrypt generados correctamente por BCryptPasswordEncoder
-- 2. Validación completa de datos de entrada
-- 3. Auditoría adecuada de creación de usuarios
-- 4. Sin credenciales hardcodeadas en el código

-- Comentario para referencia:
COMMENT ON TABLE users IS 'Sistema de usuarios dinámicos - Creación via endpoint /api/v1/auth/register'; 