package com.los.loanoriginatingsystem.kyc.audit.repository;

import com.los.loanoriginatingsystem.kyc.audit.entity.KycAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KycAuditRepository extends JpaRepository<KycAudit,String> {
}
