-- =============================================
-- MIGRACIÓN V9: Crear tabla de leads de Squarespace
-- =============================================

CREATE TABLE squarespace_leads (
    id BIGSERIAL PRIMARY KEY,
    
    -- Campos del formulario de Squarespace
    receptionist_name VARCHAR(255),
    sede VARCHAR(100),
    client_name VARCHAR(255),
    dni VARCHAR(8),
    monthly_income DECIMAL(10, 2),
    treatment_cost DECIMAL(10, 2),
    phone VARCHAR(15),
    
    -- Estado y procesamiento
    status VARCHAR(50) NOT NULL DEFAULT 'NUEVO',
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    
    -- Información adicional de tracking
    source_url VARCHAR(500),
    form_submission_id VARCHAR(100),
    user_agent VARCHAR(500),
    ip_address VARCHAR(45),
    
    -- Auditoría
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Índices para mejor performance
CREATE INDEX idx_squarespace_dni ON squarespace_leads(dni);
CREATE INDEX idx_squarespace_created_at ON squarespace_leads(created_at);
CREATE INDEX idx_squarespace_status ON squarespace_leads(status);
CREATE INDEX idx_squarespace_sede ON squarespace_leads(sede);
CREATE INDEX idx_squarespace_processed ON squarespace_leads(processed);

-- Comentarios para documentación
COMMENT ON TABLE squarespace_leads IS 'Tabla específica para leads provenientes de formularios de Squarespace';
COMMENT ON COLUMN squarespace_leads.receptionist_name IS 'Nombres y Apellidos del Recepcionista';
COMMENT ON COLUMN squarespace_leads.sede IS 'Sede seleccionada en el formulario';
COMMENT ON COLUMN squarespace_leads.client_name IS 'Nombres y Apellidos del Cliente';
COMMENT ON COLUMN squarespace_leads.dni IS 'Número de DNI del cliente';
COMMENT ON COLUMN squarespace_leads.monthly_income IS 'Ingreso mensual promedio en soles';
COMMENT ON COLUMN squarespace_leads.treatment_cost IS 'Costo aproximado del tratamiento en soles';
COMMENT ON COLUMN squarespace_leads.phone IS 'Número de teléfono de contacto';
COMMENT ON COLUMN squarespace_leads.processed IS 'Indica si el lead ya fue procesado por el equipo';
COMMENT ON COLUMN squarespace_leads.notes IS 'Notas adicionales del equipo de ventas';
COMMENT ON COLUMN squarespace_leads.form_submission_id IS 'ID único del envío del formulario';
COMMENT ON COLUMN squarespace_leads.user_agent IS 'Información del navegador del usuario';
COMMENT ON COLUMN squarespace_leads.ip_address IS 'Dirección IP del usuario que envió el formulario'; 