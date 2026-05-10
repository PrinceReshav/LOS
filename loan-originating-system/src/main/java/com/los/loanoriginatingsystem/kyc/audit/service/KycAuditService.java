package com.los.loanoriginatingsystem.kyc.audit.service;

import com.los.loanoriginatingsystem.kyc.audit.entity.KycAudit;
import com.los.loanoriginatingsystem.kyc.audit.repository.KycAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KycAuditService {

    private final KycAuditRepository repo;

    public void audit(String tempId, String type, String status,
                      String request, String response, String error) {

        KycAudit audit = new KycAudit();

        audit.setId(UUID.randomUUID().toString());
        audit.setTempId(tempId);
        audit.setKycType(type);
        audit.setStatus(status);
        audit.setRequestPayload(request);
        audit.setResponsePayload(response);
        audit.setErrorMessage(error);
        audit.setCreatedAt(LocalDateTime.now());

        repo.save(audit);
    }
}