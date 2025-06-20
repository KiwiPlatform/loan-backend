-- V1__create_initial_schema.sql
-- Initial schema creation for KiwiPay Loan Backend - Phase 1

-- Create clinics table (Catálogo básico)
CREATE TABLE IF NOT EXISTS clinics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM'
);

-- Create medical_specialties table (Catálogo básico)
CREATE TABLE IF NOT EXISTS medical_specialties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM'
);

-- Create leads table (Tabla principal para los formularios)
CREATE TABLE IF NOT EXISTS leads (
    id BIGSERIAL PRIMARY KEY,
    -- Datos del formulario
    receptionist_name VARCHAR(255) NOT NULL,
    client_name VARCHAR(255) NOT NULL,
    clinic_id BIGINT NOT NULL,
    medical_specialty_id BIGINT NOT NULL,
    dni VARCHAR(8) NOT NULL,
    monthly_income DECIMAL(10,2) NOT NULL,
    treatment_cost DECIMAL(10,2) NOT NULL,
    phone VARCHAR(9) NOT NULL,
    email VARCHAR(255),
    -- Metadatos
    status VARCHAR(50) NOT NULL DEFAULT 'NUEVO',
    origin VARCHAR(50) NOT NULL DEFAULT 'WEB',
    -- Auditoría completa
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    -- Constraints
    CONSTRAINT fk_leads_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id),
    CONSTRAINT fk_leads_medical_specialty FOREIGN KEY (medical_specialty_id) REFERENCES medical_specialties(id)
);

-- Create indexes for performance and security auditing
CREATE INDEX idx_leads_dni ON leads(dni);
CREATE INDEX idx_leads_created_at ON leads(created_at);
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_clinic_id ON leads(clinic_id);
CREATE INDEX idx_leads_medical_specialty_id ON leads(medical_specialty_id);
CREATE INDEX idx_leads_created_by ON leads(created_by);
CREATE INDEX idx_leads_updated_by ON leads(updated_by);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_clinics_updated_at BEFORE UPDATE ON clinics
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_medical_specialties_updated_at BEFORE UPDATE ON medical_specialties
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_leads_updated_at BEFORE UPDATE ON leads
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 