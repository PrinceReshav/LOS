CREATE TABLE user_visibility (
    id BIGSERIAL PRIMARY KEY,

    viewer_user_id VARCHAR(50) NOT NULL,
    target_user_id VARCHAR(50) NOT NULL,

    access_type VARCHAR(20) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(viewer_user_id, target_user_id)
);

CREATE INDEX idx_uv_viewer
ON user_visibility(viewer_user_id);

CREATE INDEX idx_uv_target
ON user_visibility(target_user_id);

CREATE INDEX idx_uv_viewer_target
ON user_visibility(viewer_user_id, target_user_id);

INSERT INTO user_visibility (viewer_user_id, target_user_id, access_type)
SELECT :viewerId, u.user_id, 'ROLE_HIERARCHY'
FROM users u
JOIN role_closure rc
  ON u.role_id = rc.descendant_role_id
WHERE rc.ancestor_role_id = :roleId
ON CONFLICT DO NOTHING;