-- Migration to make client_name and dni optional in leads table
-- V2__make_client_name_and_dni_optional.sql

-- Make client_name nullable
ALTER TABLE leads ALTER COLUMN client_name DROP NOT NULL;

-- Make dni nullable
ALTER TABLE leads ALTER COLUMN dni DROP NOT NULL;

-- Add comment to document the change
COMMENT ON COLUMN leads.client_name IS 'Client name - optional field for privacy compliance';
COMMENT ON COLUMN leads.dni IS 'Document number - optional field for privacy compliance'; 