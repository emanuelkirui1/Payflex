-- Add promotion timestamps to users
ALTER TABLE users
  ADD COLUMN promoted_to_hr_at TIMESTAMP NULL,
  ADD COLUMN promoted_to_company_admin_at TIMESTAMP NULL;

CREATE INDEX IF NOT EXISTS idx_users_promoted_to_hr_at ON users(promoted_to_hr_at);
CREATE INDEX IF NOT EXISTS idx_users_promoted_to_company_admin_at ON users(promoted_to_company_admin_at);

-- Notifications table for in-app notifications
CREATE TABLE IF NOT EXISTS notifications (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
