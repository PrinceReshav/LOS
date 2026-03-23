CREATE TABLE password_reset_token (
                                      id BIGSERIAL PRIMARY KEY,
                                      user_id VARCHAR(50) NOT NULL,
                                      token_hash VARCHAR(255) NOT NULL,
                                      expires_at TIMESTAMP NOT NULL,
                                      used BOOLEAN NOT NULL DEFAULT FALSE,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prt_user_id ON password_reset_token(user_id);
CREATE UNIQUE INDEX idx_prt_token_hash ON password_reset_token(token_hash);