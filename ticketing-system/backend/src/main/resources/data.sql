-- Insert default admin user
-- Password: admin123 (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, role, enabled, created_at, updated_at) 
VALUES ('admin', 'admin@ticketing.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System', 'Administrator', 'ADMIN', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Insert default support agent
-- Password: support123 (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, role, enabled, created_at, updated_at) 
VALUES ('support', 'support@ticketing.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Support', 'Agent', 'SUPPORT_AGENT', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Insert default user
-- Password: user123 (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, role, enabled, created_at, updated_at) 
VALUES ('user', 'user@ticketing.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Test', 'User', 'USER', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;