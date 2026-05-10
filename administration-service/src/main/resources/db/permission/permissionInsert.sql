-- ADMIN gets ALL permissions
INSERT INTO profile_permissions (profile_id, permission_id, allowed)
SELECT 'profile_admin', id, true FROM permissions;

-- SALES gets READ only
INSERT INTO profile_permissions (profile_id, permission_id, allowed)
SELECT 'profile_sales', id, true
FROM permissions
WHERE action = 'READ';