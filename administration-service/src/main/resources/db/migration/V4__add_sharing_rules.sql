CREATE TABLE sharing_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- id BIGSERIAL PRIMARY KEY for Postgres
    from_role_id VARCHAR(50) NOT NULL,
    to_role_id VARCHAR(50) NOT NULL,
    access_type VARCHAR(20),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE manual_share (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_user_id VARCHAR(50) NOT NULL,
    shared_with_user_id VARCHAR(50) NOT NULL,
    access_type VARCHAR(20)
);

CREATE INDEX idx_sr_from_role
ON sharing_rule(from_role_id);

CREATE INDEX idx_sr_to_role
ON sharing_rule(to_role_id);

CREATE INDEX idx_ms_owner
ON manual_share(owner_user_id);

CREATE INDEX idx_ms_shared
ON manual_share(shared_with_user_id);

-- ✅ ADD THESE AT THE END
ALTER TABLE sharing_rule
ADD CONSTRAINT uq_sharing_rule UNIQUE (from_role_id, to_role_id);

ALTER TABLE manual_share
ADD CONSTRAINT uq_manual_share UNIQUE (owner_user_id, shared_with_user_id);