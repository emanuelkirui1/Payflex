-- V2__add_billing.sql
CREATE TABLE billing_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2),
    features JSONB, -- or TEXT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE company_billing (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT REFERENCES companies(id),
    billing_plan_id BIGINT REFERENCES billing_plans(id),
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'active'
);

-- Add superadmin role, assume role 'superadmin'
-- Update users table if needed, but already has role

-- Insert some billing plans
INSERT INTO billing_plans (name, description, price, features) VALUES
('Basic', 'Up to 10 employees', 100.00, '{"employees": 10, "features": ["basic_payroll", "tax_calc"]}'),
('Standard', 'Up to 50 employees', 300.00, '{"employees": 50, "features": ["standard_payroll", "advanced_tax", "reports"]}'),
('Premium', 'Unlimited employees', 500.00, '{"employees": -1, "features": ["premium_payroll", "all_reports", "api_access"]}');