package com.los.loanoriginatingsystem.audit.repository;

import com.los.loanoriginatingsystem.audit.entity.ActionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActionAuditRepository extends JpaRepository<ActionAudit, String>, JpaSpecificationExecutor<ActionAudit> {

    Page<ActionAudit> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            String entityId,
            Pageable pageable
    );

}