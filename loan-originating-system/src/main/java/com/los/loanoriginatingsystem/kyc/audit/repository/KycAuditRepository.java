package com.los.loanoriginatingsystem.kyc.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KycAuditRepository extends JpaRepository<KycAudit,String> {
}
