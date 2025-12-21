
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT REFERENCES companies(id),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    company_id BIGINT REFERENCES companies(id),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    kra_pin VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE salaries (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    basic_salary NUMERIC(12,2),
    allowances NUMERIC(12,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE taxes (
    id BIGSERIAL PRIMARY KEY,
    salary_id BIGINT REFERENCES salaries(id),
    tax_amount NUMERIC(12,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payslips (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    net_pay NUMERIC(12,2),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
