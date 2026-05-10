package com.los.loanoriginatingsystem.audit.kafka;

import com.los.loanoriginatingsystem.audit.entity.ActionAudit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditKafkaProducer {

    private final KafkaTemplate<String, ActionAudit> kafkaTemplate;
    private static final String TOPIC = "los.audit";

    public void send(ActionAudit audit) {

        try {
            kafkaTemplate.send(TOPIC, audit.getId(), audit);
        } catch (Exception e) {
            log.error("Audit Kafka failed auditId={}", audit.getId(), e);
        }
    }
}