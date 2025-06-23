-- =============================================================================
-- MIGRACIÓN V8: AGREGAR CAMPO OBSERVACION
-- Descripción: Agrega el campo observacion a la tabla leads
-- Autor: Sistema de Autenticación KiwiPay  
-- Fecha: 2025-01-22
-- =============================================================================

-- Agregar campo observacion a la tabla leads
ALTER TABLE leads ADD COLUMN IF NOT EXISTS observacion TEXT;

-- Agregar comentario descriptivo
COMMENT ON COLUMN leads.observacion IS 'Observaciones adicionales sobre el lead';

-- Crear índice para búsquedas por texto (opcional)
-- CREATE INDEX IF NOT EXISTS idx_leads_observacion ON leads USING gin(to_tsvector('spanish', observacion)); 