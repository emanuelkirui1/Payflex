ALTER TABLE audit_logs
ADD COLUMN entity_type VARCHAR(100),
ADD COLUMN entity_id BIGINT,
ADD COLUMN metadata TEXT;