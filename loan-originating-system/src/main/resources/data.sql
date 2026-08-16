DELETE FROM sla_config;

INSERT INTO sla_config
(
    id,
    deviation_level,
    timeout_minutes,
    action,
    next_level,
    active
)
VALUES
('1',1,30,'ESCALATE',2,true),
('2',2,60,'ESCALATE',3,true),
('3',3,120,'AUTO_APPROVE',NULL,true);

-- =====================================================
-- LOAN SCHEME CONFIG (admin-editable scheme master)
-- =====================================================
DELETE FROM loan_scheme_config;

INSERT INTO loan_scheme_config (id, code, name, description, active, created_at, updated_at) VALUES
('LS001', 'ITR_SCHEME', 'ITR Scheme', 'Income-tax-return based income assessment scheme', true, NOW(), NOW()),
('LS002', 'ABB_SCHEME', 'ABB Scheme', 'Average bank balance based income assessment scheme', true, NOW(), NOW()),
('LS003', 'GST_SCHEME', 'GST Scheme', 'GST turnover based income assessment scheme', true, NOW(), NOW());

-- =====================================================
-- INSURANCE MATRIX (life / credit-shield rate card)
-- Age bands x tenure -> flat rate (%), with a preferential reduced rate
-- =====================================================
DELETE FROM insurance_matrix;

INSERT INTO insurance_matrix (id, min_age, max_age, tenure_months, flat_rate, flat_reduced_rate, description, active, created_at, updated_at) VALUES
('IM001', 18, 35, 12, 0.35, 0.28, '18-35 age band, 12 month tenure', true, NOW(), NOW()),
('IM002', 18, 35, 24, 0.55, 0.45, '18-35 age band, 24 month tenure', true, NOW(), NOW()),
('IM003', 18, 35, 36, 0.75, 0.62, '18-35 age band, 36 month tenure', true, NOW(), NOW()),
('IM004', 36, 50, 12, 0.45, 0.36, '36-50 age band, 12 month tenure', true, NOW(), NOW()),
('IM005', 36, 50, 24, 0.70, 0.58, '36-50 age band, 24 month tenure', true, NOW(), NOW()),
('IM006', 36, 50, 36, 0.95, 0.80, '36-50 age band, 36 month tenure', true, NOW(), NOW()),
('IM007', 51, 65, 12, 0.65, 0.55, '51-65 age band, 12 month tenure', true, NOW(), NOW()),
('IM008', 51, 65, 24, 1.00, 0.85, '51-65 age band, 24 month tenure', true, NOW(), NOW()),
('IM009', 51, 65, 36, 1.35, 1.15, '51-65 age band, 36 month tenure', true, NOW(), NOW());

-- =====================================================
-- PROPERTY INSURANCE RATES (percentage inclusive of GST)
-- =====================================================
DELETE FROM property_insurance_rate;

INSERT INTO property_insurance_rate (id, policy_tenure_months, percentage_inc_gst, description, active, created_at, updated_at) VALUES
('PIR001', 12, 0.15, '1 year policy', true, NOW(), NOW()),
('PIR002', 24, 0.28, '2 year policy', true, NOW(), NOW()),
('PIR003', 36, 0.40, '3 year policy', true, NOW(), NOW());

-- =====================================================
-- COMMERCIAL MATRIX (approval routing by scheme/type/credit-score/amount/rate+fee band)
-- =====================================================
DELETE FROM commercial_matrix;

INSERT INTO commercial_matrix
(id, name, scheme, loan_type, secured_loan_category, product_code,
 min_credit_score, max_credit_score, min_loan_amount, max_loan_amount,
 min_total, max_total, min_processing_fee, max_processing_fee,
 required_role, auto_approved, max_processing_fees_allowed, active, created_at, updated_at)
VALUES
-- Standard terms, good credit score, small ticket -> auto approve
('CM001', 'Standard small-ticket auto-approve', NULL, NULL, NULL, NULL,
 750, NULL, NULL, 500000, NULL, NULL, NULL, NULL,
 NULL, true, NULL, true, NOW(), NOW()),

-- Mid ticket, standard terms -> Cluster Business/Credit Manager
('CM002', 'Mid-ticket standard approval', NULL, NULL, NULL, NULL,
 700, NULL, 500000.01, 2000000, NULL, NULL, NULL, NULL,
 'CBM', false, NULL, true, NOW(), NOW()),

-- Large ticket -> Divisional Business Manager
('CM003', 'Large-ticket approval', NULL, NULL, NULL, NULL,
 700, NULL, 2000000.01, 5000000, NULL, NULL, NULL, NULL,
 'DBM', false, NULL, true, NOW(), NOW()),

-- Very large ticket -> Zonal Business Manager
('CM004', 'Very large-ticket approval', NULL, NULL, NULL, NULL,
 NULL, NULL, 5000000.01, NULL, NULL, NULL, NULL, NULL,
 'ZBM', false, NULL, true, NOW(), NOW()),

-- Below minimum credit score, any amount -> escalate to Business Head
('CM005', 'Sub-700 credit score escalation', NULL, NULL, NULL, NULL,
 NULL, 699, NULL, NULL, NULL, NULL, NULL, NULL,
 'BH', false, NULL, true, NOW(), NOW());

-- =====================================================
-- APPROVAL MATRIX (deviation-level -> approver role chain)
-- =====================================================
DELETE FROM approval_matrix;

INSERT INTO approval_matrix (id, level, required_role, sequence, deviation_type, active) VALUES
('AM001', 1, 'CBM', 1, NULL, true),
('AM002', 1, 'CCM', 2, NULL, true),
('AM003', 2, 'DBM', 1, NULL, true),
('AM004', 2, 'DCM', 2, NULL, true),
('AM005', 3, 'ZBM', 1, NULL, true),
('AM006', 3, 'ZCM', 2, NULL, true);


-- =====================================================
-- DOCUMENT TEMPLATES (starter set - edit freely via /admin/document-templates)
-- =====================================================
DELETE FROM document_template;

INSERT INTO document_template (id, code, name, description, applicable_stage, html_content, version, active, created_at, updated_at)
VALUES
('DT001', 'SANCTION_LETTER', 'Sanction Letter', 'Issued once a loan is sanctioned', 'Sanctioned',
'<html><body style="font-family:Helvetica,Arial,sans-serif;padding:40px;">
<h2>Loan Sanction Letter</h2>
<p>Date: <span th:text="${generatedOn}">--</span></p>
<p>Dear <span th:text="${applicantName}">Applicant</span>,</p>
<p>We are pleased to inform you that your loan application
<b th:text="${applicationNumber}">APP-0000</b> has been sanctioned on the following terms:</p>
<table style="border-collapse:collapse;width:100%;" border="1" cellpadding="6">
<tr><td>Approved Amount</td><td th:text="${approvedAmount}">0</td></tr>
<tr><td>Rate of Interest</td><td th:text="${roi} + ''%'">0%</td></tr>
<tr><td>Tenure (months)</td><td th:text="${tenureMonths}">0</td></tr>
<tr><td>EMI</td><td th:text="${emiAmount}">0</td></tr>
<tr><td>Branch</td><td th:text="${branchName}">--</td></tr>
</table>
<p style="margin-top:30px;">This sanction is subject to the terms and conditions of the loan agreement.</p>
<p>Regards,<br/>Loan Originating System</p>
</body></html>',
1, true, NOW(), NOW()),

('DT002', 'WELCOME_LETTER', 'Welcome Letter', 'Sent when disbursement is initiated', 'Initiate Disbursement',
'<html><body style="font-family:Helvetica,Arial,sans-serif;padding:40px;">
<h2>Welcome Letter</h2>
<p>Date: <span th:text="${generatedOn}">--</span></p>
<p>Dear <span th:text="${applicantName}">Applicant</span>,</p>
<p>Welcome aboard! Your loan account <b th:text="${loanAccountNumber}">LN-0000</b>
(application <span th:text="${applicationNumber}">APP-0000</span>) is being processed for disbursement.</p>
<p>Loan Amount: <span th:text="${approvedAmount}">0</span><br/>
EMI: <span th:text="${emiAmount}">0</span><br/>
Bank Account: <span th:text="${accountHolderName}">--</span> / <span th:text="${bankName}">--</span></p>
<p>Regards,<br/>Loan Originating System</p>
</body></html>',
1, true, NOW(), NOW()),

('DT003', 'REJECTION_LETTER', 'Rejection Letter', 'Sent when a loan application is rejected', 'Rejected',
'<html><body style="font-family:Helvetica,Arial,sans-serif;padding:40px;">
<h2>Application Status</h2>
<p>Date: <span th:text="${generatedOn}">--</span></p>
<p>Dear <span th:text="${applicantName}">Applicant</span>,</p>
<p>We regret to inform you that your loan application
<b th:text="${applicationNumber}">APP-0000</b> could not be approved at this time.</p>
<p>Regards,<br/>Loan Originating System</p>
</body></html>',
1, true, NOW(), NOW());


-- =====================================================
-- STAMP DUTY CONFIG (state-wise)
-- =====================================================
DELETE FROM stamp_duty_config;

INSERT INTO stamp_duty_config (id, state_code, state_name, stamp_duty_percent, flat_fee, description, active, created_at, updated_at) VALUES
('SD001', 'MH', 'Maharashtra', 0.20, 100, 'Standard loan agreement stamp duty', true, NOW(), NOW()),
('SD002', 'DL', 'Delhi', 0.25, 100, 'Standard loan agreement stamp duty', true, NOW(), NOW()),
('SD003', 'KA', 'Karnataka', 0.15, 200, 'Standard loan agreement stamp duty', true, NOW(), NOW()),
('SD004', 'UP', 'Uttar Pradesh', 0.25, 50, 'Standard loan agreement stamp duty', true, NOW(), NOW()),
('SD005', 'TN', 'Tamil Nadu', 0.20, 100, 'Standard loan agreement stamp duty', true, NOW(), NOW());

-- =====================================================
-- GENERAL CONFIG (key/value app settings)
-- =====================================================
DELETE FROM general_config;

INSERT INTO general_config (id, config_key, config_value, description, active, created_at, updated_at) VALUES
('GC001', 'AML_SCORE_THRESHOLD', '70', 'Minimum AML score below which a manual review is required', true, NOW(), NOW()),
('GC002', 'SECURED_LOAN_CHARGES_LABEL', 'Secured Loan Processing Charges', 'Display label used on generated documents', true, NOW(), NOW()),
('GC003', 'UNSECURED_LOAN_CHARGES_LABEL', 'Unsecured Loan Processing Charges', 'Display label used on generated documents', true, NOW(), NOW()),
('GC004', 'DEFAULT_CREDIT_SCORE_VALIDITY_DAYS', '30', 'Number of days a fetched credit score stays valid before re-fetch is required', true, NOW(), NOW());
