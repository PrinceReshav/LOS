package com.los.loanoriginatingsystem.audit.service;

import com.los.loanoriginatingsystem.audit.entity.ActionAudit;
import com.los.loanoriginatingsystem.audit.kafka.AuditKafkaProducer;
import com.los.loanoriginatingsystem.audit.repository.ActionAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ActionAuditRepository repo;
    private final AuditKafkaProducer kafkaProducer;

    public void log(String entityType,
                    String entityId,
                    String action,
                    String oldStatus,
                    String newStatus,
                    String remarks) {

        String username = "SYSTEM";
        String role = "SYSTEM";

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            username = auth.getName();

            role = auth.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("UNKNOWN");
        }

        ActionAudit audit = new ActionAudit();

        audit.setId(UUID.randomUUID().toString());
        audit.setEntityType(entityType);
        audit.setEntityId(entityId);
        audit.setAction(action);
        audit.setPerformedBy(username);
        audit.setRole(role);
        audit.setOldStatus(oldStatus);
        audit.setNewStatus(newStatus);
        audit.setRemarks(remarks);
        audit.setCreatedAt(LocalDateTime.now());

        repo.save(audit);
    }

    private String generateHash(ActionAudit audit, String previousHash) {

        String data = audit.getEntityId()
                + audit.getAction()
                + audit.getOldStatus()
                + audit.getNewStatus()
                + audit.getCreatedAt()
                + previousHash;

        return DigestUtils.sha256Hex(data);
    }
    public boolean verifyChain(List<ActionAudit> audits) {

        audits.sort(Comparator.comparing(ActionAudit::getCreatedAt));

        for (int i = 1; i < audits.size(); i++) {

            String expectedHash = generateHash(
                    audits.get(i),
                    audits.get(i - 1).getHash()
            );

            if (!expectedHash.equals(audits.get(i).getHash())) {
                return false;
            }
        }

        return true;
    }
}