-- =============================================================================
-- MIGRACIÓN V7: AGREGAR COLUMNAS DE SPRING SECURITY FALTANTES
-- Descripción: Agrega las columnas de UserDetails necesarias para Spring Security
-- Autor: Sistema de Autenticación KiwiPay  
-- Fecha: 2025-01-22
-- =============================================================================

-- Agregar columnas de Spring Security UserDetails que faltaban
ALTER TABLE users ADD COLUMN IF NOT EXISTS account_non_expired BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE users ADD COLUMN IF NOT EXISTS account_non_locked BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE users ADD COLUMN IF NOT EXISTS credentials_non_expired BOOLEAN NOT NULL DEFAULT true;

-- Actualizar usuarios existentes para asegurar valores correctos
UPDATE users SET 
    account_non_expired = true,
    account_non_locked = true, 
    credentials_non_expired = true
WHERE account_non_expired IS NULL 
   OR account_non_locked IS NULL 
   OR credentials_non_expired IS NULL;

-- Agregar comentarios descriptivos
COMMENT ON COLUMN users.account_non_expired IS 'Indica si la cuenta no ha expirado (Spring Security)';
COMMENT ON COLUMN users.account_non_locked IS 'Indica si la cuenta no está bloqueada (Spring Security)';
COMMENT ON COLUMN users.credentials_non_expired IS 'Indica si las credenciales no han expirado (Spring Security)';

-- Crear índices para optimizar consultas de seguridad
CREATE INDEX IF NOT EXISTS idx_users_account_status ON users(enabled, account_non_expired, account_non_locked, credentials_non_expired); 