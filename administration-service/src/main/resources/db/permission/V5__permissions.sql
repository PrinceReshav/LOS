CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    object_name VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL
);

CREATE TABLE profile_permissions (
    id BIGSERIAL PRIMARY KEY,
    profile_id VARCHAR(50) NOT NULL,
    permission_id BIGINT NOT NULL,
    allowed BOOLEAN DEFAULT TRUE
);

CREATE UNIQUE INDEX uq_profile_permission
ON profile_permissions(profile_id, permission_id);

CREATE INDEX idx_perm_object
ON permissions(object_name);

CREATE INDEX idx_profile_perm_profile
ON profile_permissions(profile_id);

INSERT INTO permissions (object_name, action) VALUES
('USER', 'CREATE'),
('USER', 'READ'),
('USER', 'UPDATE'),
('USER', 'DELETE');