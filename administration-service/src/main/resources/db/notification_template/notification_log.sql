CREATE TABLE notification_log (
    id VARCHAR(50) PRIMARY KEY,
    template_code VARCHAR(100),
    type VARCHAR(20),
    recipients TEXT,
    subject TEXT,
    body TEXT,
    status VARCHAR(20), -- PENDING / SENT / FAILED
    retry_count INT,
    last_attempt_at TIMESTAMP,
    next_retry_at TIMESTAMP,
    created_at TIMESTAMP
);