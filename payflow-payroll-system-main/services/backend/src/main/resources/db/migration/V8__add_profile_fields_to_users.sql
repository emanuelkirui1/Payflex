-- Add profile fields to users table
ALTER TABLE users
  ADD COLUMN display_name VARCHAR(255),
  ADD COLUMN phone VARCHAR(50),
  ADD COLUMN job_title VARCHAR(255),
  ADD COLUMN department VARCHAR(255),
  ADD COLUMN bio TEXT,
  ADD COLUMN avatar_url VARCHAR(512);

CREATE INDEX IF NOT EXISTS idx_users_company_job_title ON users(company_id, job_title);
