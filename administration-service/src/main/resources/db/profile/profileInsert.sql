INSERT INTO profiles
(
    profile_id,
    profile_name,
    description,
    active,
    system_defined
)
VALUES
(
    'profile_admin',
    'ADMIN_PROFILE',
    'Admin Profile',
    true,
    true
),
(
    'profile_sales',
    'SALES_PROFILE',
    'Sales Profile',
    true,
    false
);