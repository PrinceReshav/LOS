-- =====================================================================
-- PERMISSION MASTER + PROFILE PERMISSION MATRIX SEED
--
-- One Permission row per module. Each Profile gets a ProfilePermission
-- row per module carrying the five action flags (Read/Create/Edit/
-- Delete/Approve) - this is what SecurityPermissionService checks on
-- every @RequiresPermission-annotated endpoint. Table/column names must
-- match the JPA entities exactly since ddl-auto=create builds the schema
-- from those entities, not from this file.
-- =====================================================================

INSERT INTO permissions (id, permission_code, permission_name, module_name, description, active, created_at, updated_at) VALUES
('PERM_USER',       'USER_MODULE',       'User Management',       'USER',       'Create, view, and manage user accounts',        true, now(), now()),
('PERM_ROLE',       'ROLE_MODULE',       'Role Management',       'ROLE',       'Create, view, and manage roles',                true, now(), now()),
('PERM_PROFILE',    'PROFILE_MODULE',    'Profile Management',    'PROFILE',    'Create, view, and manage profiles',             true, now(), now()),
('PERM_PERMISSION', 'PERMISSION_MODULE', 'Permission Management', 'PERMISSION', 'Manage the permission matrix itself',           true, now(), now()),
('PERM_VISIBILITY', 'VISIBILITY_MODULE', 'Visibility & Sharing',  'VISIBILITY', 'Manage org-wide defaults and sharing rules',    true, now(), now()),
('PERM_NOTIFICATION','NOTIFICATION_MODULE','Notifications',       'NOTIFICATION','Manage notification templates and logs',       true, now(), now()),
('PERM_AUDIT',      'AUDIT_MODULE',      'Audit Log',             'AUDIT',      'View system audit trail',                       true, now(), now());

-- ADMIN_PROFILE: full access to every module
INSERT INTO profile_permissions (profile_id, permission_id, can_read, can_create, can_edit, can_delete, can_approve) VALUES
('profile_admin', 'PERM_USER',        true, true, true, true, true),
('profile_admin', 'PERM_ROLE',        true, true, true, true, true),
('profile_admin', 'PERM_PROFILE',     true, true, true, true, true),
('profile_admin', 'PERM_PERMISSION',  true, true, true, true, true),
('profile_admin', 'PERM_VISIBILITY',  true, true, true, true, true),
('profile_admin', 'PERM_NOTIFICATION',true, true, true, true, true),
('profile_admin', 'PERM_AUDIT',       true, true, true, true, true);

-- SALES_PROFILE: read-only on Users, nothing else
INSERT INTO profile_permissions (profile_id, permission_id, can_read, can_create, can_edit, can_delete, can_approve) VALUES
('profile_sales', 'PERM_USER', true, false, false, false, false);
