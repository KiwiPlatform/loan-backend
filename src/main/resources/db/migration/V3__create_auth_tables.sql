-- =============================================================================
-- MIGRACIÓN V3: TABLAS DE AUTENTICACIÓN
-- Descripción: Crea las tablas necesarias para JWT authentication
-- Autor: Sistema de Autenticación KiwiPay
-- Fecha: 2024-12-20
-- =============================================================================

-- Tabla usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    enabled BOOLEAN NOT NULL DEFAULT true,
    
    -- Campos de auditoría (heredados de BaseEntity)
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    
    -- Índices para optimizar consultas
    CONSTRAINT users_username_check CHECK (LENGTH(username) >= 3),
    CONSTRAINT users_email_check CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Índices adicionales para mejorar performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_enabled ON users(enabled);

-- NOTA: Los usuarios se crearán dinámicamente usando el endpoint /api/v1/auth/register
-- No se insertan usuarios por defecto por seguridad

-- Comentarios sobre la estructura
COMMENT ON TABLE users IS 'Tabla de usuarios para autenticación JWT - Sin usuarios por defecto';
COMMENT ON COLUMN users.username IS 'Nombre de usuario único (mínimo 3 caracteres)';
COMMENT ON COLUMN users.password IS 'Contraseña hasheada con BCrypt';
COMMENT ON COLUMN users.email IS 'Email único del usuario';
COMMENT ON COLUMN users.role IS 'Rol del usuario: ADMIN (acceso total) o USER (acceso limitado)';
COMMENT ON COLUMN users.enabled IS 'Indica si el usuario está activo'; 