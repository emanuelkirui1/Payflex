INSERT INTO companies (name, created_at) VALUES ('Test Company', CURRENT_TIMESTAMP);

INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'admin@test.com', 'password', 'EMPLOYEE', true, CURRENT_TIMESTAMP);
INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'super@test.com', 'password', 'SUPERADMIN', true, CURRENT_TIMESTAMP);

INSERT INTO billing_plans (name, description, price, features, created_at) VALUES ('Basic', 'Up to 10 employees', 100.00, '{"employees": 10, "features": ["basic_payroll", "tax_calc"]}', CURRENT_TIMESTAMP);
INSERT INTO billing_plans (name, description, price, features, created_at) VALUES ('Standard', 'Up to 50 employees', 300.00, '{"employees": 50, "features": ["standard_payroll", "advanced_tax", "reports"]}', CURRENT_TIMESTAMP);
INSERT INTO billing_plans (name, description, price, features, created_at) VALUES ('Premium', 'Unlimited employees', 500.00, '{"employees": -1, "features": ["premium_payroll", "all_reports", "api_access"]}', CURRENT_TIMESTAMP);

INSERT INTO company_billing (company_id, billing_plan_id, start_date, status) VALUES (1, 1, CURRENT_TIMESTAMP, 'active');

-- Sample users and employees for demo/testing
INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'john.doe@test.com', 'password', 'EMPLOYEE', true, CURRENT_TIMESTAMP);
INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'jane.smith@test.com', 'password', 'EMPLOYEE', true, CURRENT_TIMESTAMP);
INSERT INTO users (company_id, email, password, role, enabled, created_at) VALUES (1, 'bob.williams@test.com', 'password', 'EMPLOYEE', true, CURRENT_TIMESTAMP);

-- Link employees to the created users
INSERT INTO employees (created_at, first_name, kra_pin, last_name, company_id, user_id)
SELECT CURRENT_TIMESTAMP, 'John', 'KRA123456', 'Doe', 1, id FROM users WHERE email='john.doe@test.com';
INSERT INTO employees (created_at, first_name, kra_pin, last_name, company_id, user_id)
SELECT CURRENT_TIMESTAMP, 'Jane', 'KRA654321', 'Smith', 1, id FROM users WHERE email='jane.smith@test.com';
INSERT INTO employees (created_at, first_name, kra_pin, last_name, company_id, user_id)
SELECT CURRENT_TIMESTAMP, 'Bob', 'KRA999999', 'Williams', 1, id FROM users WHERE email='bob.williams@test.com';

-- (Optional) Salaries, payroll runs and payslips were intentionally omitted from the import script
-- due to SQL dialect differences across H2 versions. Use the API to create salaries and runs
-- for the sample employees or run an ad-hoc SQL step via the H2 console if needed.

-- Example: to create payslips for the sample employees, run the payroll generation endpoint
-- or insert records manually via the H2 console at /h2-console.