INSERT INTO roles
(role_id, role_name, role_type, description, active,system_defined)
VALUES

('role_admin', 'ADMIN', 'ROOT',
 'System Administrator', true, true),

('role_sales', 'SALES', 'STANDARD',
 'Sales User', true, false);