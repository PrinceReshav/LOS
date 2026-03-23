CREATE TABLE notification_dlq (
    id VARCHAR(50) PRIMARY KEY,
    original_log_id VARCHAR(50),
    template_code VARCHAR(100),
    type VARCHAR(20),
    recipients TEXT,
    subject TEXT,
    body TEXT,
    failure_reason TEXT,
    created_at TIMESTAMP
);