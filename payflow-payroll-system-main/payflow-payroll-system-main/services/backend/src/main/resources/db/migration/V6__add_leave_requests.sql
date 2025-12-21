CREATE TABLE leave_requests (
  id BIGSERIAL PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  reason TEXT,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE leave_request_approvals (
  id BIGSERIAL PRIMARY KEY,
  leave_request_id BIGINT NOT NULL,
  approver_id BIGINT,
  approver_email VARCHAR(255),
  role VARCHAR(64),
  decision VARCHAR(32),
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_leave_request_approvals_leave_request FOREIGN KEY (leave_request_id) REFERENCES leave_requests(id)
);

CREATE INDEX idx_leave_request_approvals_request ON leave_request_approvals(leave_request_id);
