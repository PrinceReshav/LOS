package com.los.loanoriginatingsystem.rules.repository;

import com.los.loanoriginatingsystem.rules.entity.DeviationAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviationAuditRepository extends JpaRepository<DeviationAudit, String> {
    List<DeviationAudit> findByDeviationId(String deviationId);
}