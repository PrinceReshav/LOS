package com.los.loanoriginatingsystem.rules.service;

import com.los.loanoriginatingsystem.rules.entity.DeviationAudit;
import com.los.loanoriginatingsystem.rules.repository.DeviationAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviationAuditService {

    private final DeviationAuditRepository auditRepository;


    public void log(String deviationId,
                    String action,
                    Integer level,
                    String user,
                    String comment) {

        DeviationAudit audit = new DeviationAudit();

        audit.setId(UUID.randomUUID().toString());
        audit.setDeviationId(deviationId);
        audit.setAction(action);
        audit.setLevel(level);
        audit.setUser(user);
        audit.setComment(comment);
        audit.setTimestamp(LocalDateTime.now());

        auditRepository.save(audit);
    }
}