CREATE TABLE notification_template (
    id VARCHAR(50) PRIMARY KEY,
    code VARCHAR(100) UNIQUE, -- IDENTIFIER
    type VARCHAR(20),         -- EMAIL / SMS
    subject TEXT,
    body TEXT,
    active BOOLEAN
);

INSERT INTO notification_template VALUES
('1', 'DEVIATION_CREATED', 'EMAIL',
 'Deviation Alert',
 'Hello {{name}}, deviation created for application {{applicationId}}',
 true),

('2', 'DEVIATION_APPROVED', 'EMAIL',
 'Deviation Approved',
 'Deviation {{deviationId}} has been approved by {{user}}',
 true);