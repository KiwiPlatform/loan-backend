-- Migración V6: Crear usuario de prueba para desarrollo
-- Solo se ejecuta una vez y crea credenciales conocidas para testing

-- Insertar usuario administrador de prueba
-- Username: admin
-- Password: admin123 (se guarda hasheado con BCrypt)
-- Role: ADMIN
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at) 
VALUES (
    'admin',
    'admin@kiwipay.pe',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- BCrypt hash de "admin123"
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Insertar usuario normal de prueba  
-- Username: user
-- Password: user123 (se guarda hasheado con BCrypt)
-- Role: USER
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at)
VALUES (
    'user',
    'user@kiwipay.pe', 
    '$2a$10$9Y9bT.47xZXf6./1z8c.o.KQEvKvQ5.mOCF7wh0nFo5.rBKKhKk1y', -- BCrypt hash de "user123"
    'USER',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Comentarios para desarrollo
-- Usuario admin de prueba - Credenciales: admin/admin123 