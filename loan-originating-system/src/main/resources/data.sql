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