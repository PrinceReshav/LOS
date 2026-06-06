INSERT INTO users
(
 user_id,
 username,
 email,
 mobile,
 alias,
 first_name,
 last_name,
 employee_id,
 role_id,
 profile_id,
 active,
 created_by,
 updated_by
)
VALUES
(
 'USR_ADMIN',
 'admin',
 'prince.reshav.5555@gmail.com',
 '9999999999',
 'SYSTEM_ADMIN',
 'System',
 'Admin',
 'SYS001',
 (SELECT id FROM roles WHERE role_name='ADMIN'),
 (SELECT id FROM profiles WHERE profile_name='ADMIN_PROFILE'),
 true,
 'SYSTEM',
 'SYSTEM'
);