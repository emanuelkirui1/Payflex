CREATE TABLE payroll_run_approvals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  payroll_run_id BIGINT NOT NULL,
  approver_id BIGINT,
  approver_email VARCHAR(255),
  role VARCHAR(50),
  decision VARCHAR(20),
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payroll_run_approvals_payroll_run FOREIGN KEY (payroll_run_id) REFERENCES payroll_runs(id)
);
CREATE INDEX idx_pr_approvals_run ON payroll_run_approvals(payroll_run_id);
