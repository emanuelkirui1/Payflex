INSERT INTO companies (name, created_at) VALUES ('Test Company', CURRENT_TIMESTAMP);

INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'admin@test.com', 'password', 'EMPLOYEE', true, CURRENT_TIMESTAMP);
INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'super@test.com', 'password', 'SUPERADMIN', true, CURRENT_TIMESTAMP);

INSERT INTO billing_plans (name, description, price, features, created_at) VALUES ('Basic', 'Up to 10 employees', 100.00, '{"employees": 10, "features": ["basic_payroll", "tax_calc"]}', CURRENT_TIMESTAMP);
INSERT INTO billing_plans (name, description, price, features, created_at) VALUES ('Standard', 'Up to 50 employees', 300.00, '{"employees": 50, "features": ["standard_payroll", "advanced_tax", "reports"]}', CURRENT_TIMESTAMP);
INSERT INTO billing_plans (name, description, price, features, created_at) VALUES ('Premium', 'Unlimited employees', 500.00, '{"employees": -1, "features": ["premium_payroll", "all_reports", "api_access"]}', CURRENT_TIMESTAMP);

INSERT INTO company_billing (company_id, billing_plan_id, start_date, status) VALUES (1, 1, CURRENT_TIMESTAMP, 'active');