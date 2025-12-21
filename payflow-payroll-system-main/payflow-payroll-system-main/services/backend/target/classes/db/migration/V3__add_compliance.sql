-- Add compliance-related columns to salaries
ALTER TABLE salaries ADD COLUMN gross_salary NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN nssf_deduction NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN nhif_deduction NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN pension_contribution NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN taxable_income NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN paye_tax NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN total_deductions NUMERIC(12,2);
ALTER TABLE salaries ADD COLUMN net_pay NUMERIC(12,2);

-- Add KRA PIN to employees if not present
ALTER TABLE employees ADD COLUMN IF NOT EXISTS kra_pin VARCHAR(50);

-- Create tax_brackets table
CREATE TABLE tax_brackets (
    id BIGSERIAL PRIMARY KEY,
    min_amount NUMERIC(12,2) NOT NULL,
    max_amount NUMERIC(12,2),
    rate NUMERIC(5,4) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE
);

-- Create deduction_brackets table
CREATE TABLE deduction_brackets (
    id BIGSERIAL PRIMARY KEY,
    min_salary NUMERIC(12,2) NOT NULL,
    max_salary NUMERIC(12,2),
    deduction_amount NUMERIC(12,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE
);

-- Create contribution_rates table
CREATE TABLE contribution_rates (
    id BIGSERIAL PRIMARY KEY,
    rate NUMERIC(5,4) NOT NULL,
    max_pensionable_earnings NUMERIC(12,2),
    type VARCHAR(50) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE
);

-- Insert initial Kenyan PAYE brackets (2024/2025)
INSERT INTO tax_brackets (min_amount, max_amount, rate, effective_from) VALUES
(0, 24000, 0.10, '2024-07-01'),
(24001, 32333, 0.15, '2024-07-01'),
(32334, 500000, 0.20, '2024-07-01'),
(500001, 800000, 0.25, '2024-07-01'),
(800001, NULL, 0.30, '2024-07-01');

-- Insert initial NHIF brackets (2024)
INSERT INTO deduction_brackets (min_salary, max_salary, deduction_amount, type, effective_from) VALUES
(0, 5999, 150, 'NHIF', '2024-01-01'),
(6000, 7999, 300, 'NHIF', '2024-01-01'),
(8000, 11999, 400, 'NHIF', '2024-01-01'),
(12000, 14999, 500, 'NHIF', '2024-01-01'),
(15000, 19999, 600, 'NHIF', '2024-01-01'),
(20000, 24999, 750, 'NHIF', '2024-01-01'),
(25000, 29999, 850, 'NHIF', '2024-01-01'),
(30000, 34999, 900, 'NHIF', '2024-01-01'),
(35000, 39999, 950, 'NHIF', '2024-01-01'),
(40000, 44999, 1000, 'NHIF', '2024-01-01'),
(45000, 49999, 1100, 'NHIF', '2024-01-01'),
(50000, 59999, 1200, 'NHIF', '2024-01-01'),
(60000, 69999, 1300, 'NHIF', '2024-01-01'),
(70000, 79999, 1400, 'NHIF', '2024-01-01'),
(80000, 89999, 1500, 'NHIF', '2024-01-01'),
(90000, 99999, 1600, 'NHIF', '2024-01-01'),
(100000, NULL, 1700, 'NHIF', '2024-01-01');

-- Insert NSSF rates
INSERT INTO contribution_rates (rate, max_pensionable_earnings, type, effective_from) VALUES
(0.06, 18000, 'NSSF_EMPLOYEE', '2024-01-01'),
(0.06, 18000, 'NSSF_EMPLOYER', '2024-01-01');