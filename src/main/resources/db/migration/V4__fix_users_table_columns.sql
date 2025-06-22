-- =============================================================================
-- MIGRACIÓN V4: CORRECCIÓN DE COLUMNAS EN TABLA USERS
-- Descripción: Renombra las columnas de auditoría para coincidir con BaseEntity
-- Autor: Sistema de Autenticación KiwiPay  
-- Fecha: 2024-12-21
-- =============================================================================

-- Renombrar columnas de auditoría para coincidir con BaseEntity
-- De created_date a created_at
ALTER TABLE users RENAME COLUMN created_date TO created_at;

-- De last_modified_date a updated_at  
ALTER TABLE users RENAME COLUMN last_modified_date TO updated_at;

-- De last_modified_by a updated_by
ALTER TABLE users RENAME COLUMN last_modified_by TO updated_by;

-- Ampliar el tamaño de las columnas created_by y updated_by a 100 caracteres para coincidir con BaseEntity
ALTER TABLE users ALTER COLUMN created_by TYPE VARCHAR(100);
ALTER TABLE users ALTER COLUMN updated_by TYPE VARCHAR(100);

-- Actualizar los registros existentes para llenar updated_by donde esté NULL
UPDATE users SET updated_by = created_by WHERE updated_by IS NULL;

-- Comentarios actualizados
COMMENT ON COLUMN users.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN users.updated_at IS 'Fecha y hora de última modificación del registro';
COMMENT ON COLUMN users.created_by IS 'Usuario que creó el registro';
COMMENT ON COLUMN users.updated_by IS 'Usuario que modificó el registro por última vez'; 