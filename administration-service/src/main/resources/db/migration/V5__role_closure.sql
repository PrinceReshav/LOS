CREATE TABLE role_closure (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    ancestor_role_id VARCHAR(50) NOT NULL,
    descendant_role_id VARCHAR(50) NOT NULL,

    depth INT NOT NULL, -- 0=self, 1=direct child, etc.

    UNIQUE (ancestor_role_id, descendant_role_id)
);

CREATE INDEX idx_rc_ancestor
ON role_closure(ancestor_role_id);

CREATE INDEX idx_rc_descendant
ON role_closure(descendant_role_id);