INSERT INTO field_permissions (profile_id, object_name, field_name, can_read, can_write, masked)
VALUES
('profile_admin', 'USER', 'email', true, true, false),
('profile_admin', 'USER', 'mobile', true, true, false),

('profile_sales', 'USER', 'email', false, false, false),
('profile_sales', 'USER', 'mobile', true, false, true);