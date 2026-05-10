CREATE TABLE field_permissions (
    id BIGSERIAL PRIMARY KEY,

    profile_id VARCHAR(50) NOT NULL,
    object_name VARCHAR(50) NOT NULL,
    field_name VARCHAR(50) NOT NULL,

    can_read BOOLEAN DEFAULT TRUE,
    can_write BOOLEAN DEFAULT FALSE,
    masked BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_field_perm_profile
ON field_permissions(profile_id);

CREATE INDEX idx_field_perm_object
ON field_permissions(object_name);


-- ADMIN → full access
INSERT INTO field_permissions
(profile_id, object_name, field_name, can_read, can_write, masked)
VALUES
('profile_admin', 'USER', 'email', true, true, false),
('profile_admin', 'USER', 'mobile', true, true, false);

-- SALES → restricted
INSERT INTO field_permissions
(profile_id, object_name, field_name, can_read, can_write, masked)
VALUES
('profile_sales', 'USER', 'email', false, false, false),
('profile_sales', 'USER', 'mobile', true, false, true);