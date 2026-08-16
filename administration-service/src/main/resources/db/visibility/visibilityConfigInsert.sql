DELETE FROM visibility_config WHERE entity_name IN ('USER', 'LOAN_APPLICATION');

INSERT INTO visibility_config (entity_name, visibility_type) VALUES
('USER', 'PRIVATE'),
('LOAN_APPLICATION', 'PRIVATE');
