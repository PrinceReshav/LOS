-- NOTE: table creation is handled by Hibernate (spring.jpa.hibernate.ddl-auto=create)
-- from the NotificationTemplate entity - this file only seeds rows, and was
-- previously never actually loaded (missing from spring.sql.init.data-locations),
-- which is why every NotificationService.send() call failed with
-- "Template not found" even after the calling service's URL was fixed.

DELETE FROM notification_template;

INSERT INTO notification_template (id, code, type, subject, body, active) VALUES
('NT001', 'DEVIATION_CREATED', 'EMAIL',
 'Deviation Alert',
 'Hello {{name}}, deviation created for application {{applicationId}}',
 true),

('NT002', 'DEVIATION_APPROVED', 'EMAIL',
 'Deviation Approved',
 'Deviation {{deviationId}} has been approved by {{user}}',
 true),

('NT003', 'SYSTEM_FAILURE_ALERT', 'EMAIL',
 '🚨 Loan Processing Failed',
 'TempId: {{tempId}} | LeadId: {{leadId}} | Reason: {{reason}}',
 true),

('NT004', 'LOAN_SANCTIONED', 'EMAIL',
 'Your Loan Has Been Sanctioned',
 'Dear {{applicantName}}, your loan application {{applicationNumber}} has been sanctioned for {{approvedAmount}}.',
 true),

('NT005', 'DISBURSEMENT_INITIATED', 'EMAIL',
 'Disbursement Initiated',
 'Dear {{applicantName}}, disbursement has been initiated for your application {{applicationNumber}}.',
 true),

('NT006', 'LOAN_DISBURSED', 'EMAIL',
 'Loan Disbursed',
 'Dear {{applicantName}}, your loan account {{loanAccountNumber}} has been disbursed successfully.',
 true),

('NT007', 'LOAN_REJECTED', 'EMAIL',
 'Application Status Update',
 'Dear {{applicantName}}, your loan application {{applicationNumber}} could not be approved. Reason: {{rejectionReason}}.',
 true);
