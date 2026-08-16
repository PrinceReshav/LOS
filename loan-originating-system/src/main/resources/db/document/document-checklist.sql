DELETE FROM document_checklist;

INSERT INTO document_checklist
(
    id,
    loan_product_code,
    document_type,
    auto_generated,
    active,
    description
)
VALUES
('DC001', 'LP001', 'Aadhaar Front', false, true, 'Aadhaar card - front side'),
('DC002', 'LP001', 'PAN Card', false, true, 'PAN card'),
('DC003', 'LP001', 'Customer Photo', false, true, 'Recent passport-size photo'),
('DC004', 'LP001', 'Driving License', false, true, 'Driving license (if available)'),
('DC005', 'LP001', 'Voter Id', false, true, 'Voter ID (if available)'),
('DC006', 'LP001', 'Bank Statement', false, true, 'Last 6 months bank statement');
