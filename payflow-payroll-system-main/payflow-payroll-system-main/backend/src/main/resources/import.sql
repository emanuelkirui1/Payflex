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

-- Add sample salaries (basic + allowances + simple deductions)
INSERT INTO salaries (allowances, basic_salary, created_at, gross_salary, net_pay, nhif_deduction, nssf_deduction, paye_tax, pension_contribution, taxable_income, total_deductions, employee_id)
SELECT 20000, 100000, CURRENT_TIMESTAMP, 120000, 100000, 2000, 5000, 13000, 0, 105000, 20000, id FROM employees WHERE first_name='John' AND last_name='Doe';
INSERT INTO salaries (allowances, basic_salary, created_at, gross_salary, net_pay, nhif_deduction, nssf_deduction, paye_tax, pension_contribution, taxable_income, total_deductions, employee_id)
SELECT 10000, 80000, CURRENT_TIMESTAMP, 90000, 75000, 1500, 4000, 12500, 0, 82000, 15000, id FROM employees WHERE first_name='Jane' AND last_name='Smith';
INSERT INTO salaries (allowances, basic_salary, created_at, gross_salary, net_pay, nhif_deduction, nssf_deduction, paye_tax, pension_contribution, taxable_income, total_deductions, employee_id)
SELECT 5000, 50000, CURRENT_TIMESTAMP, 55000, 45000, 1000, 3000, 8000, 0, 49500, 10000, id FROM employees WHERE first_name='Bob' AND last_name='Williams';

-- Create a sample payroll run (DRAFT) covering the company employees
INSERT INTO payroll_runs (approved_by, created_at, created_by, run_date, status, total_deductions, total_gross, total_net, company_id)
VALUES (NULL, CURRENT_TIMESTAMP, 'super@test.com', CURRENT_TIMESTAMP, 'DRAFT', 45000, 265000, 220000, 1);

-- Generate payslips for the sample employees and attach them to the created payroll run
INSERT INTO payslips (generated_at, net_pay, employee_id, payroll_run_id)
SELECT CURRENT_TIMESTAMP, s.net_pay, e.id, (SELECT id FROM payroll_runs WHERE created_by='super@test.com' ORDER BY id DESC LIMIT 1)
FROM salaries s JOIN employees e ON s.employee_id = e.id
WHERE e.first_name IN ('John','Jane','Bob');