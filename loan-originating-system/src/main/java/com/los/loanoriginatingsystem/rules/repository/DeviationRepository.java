package com.los.loanoriginatingsystem.rules.repository;

import com.los.loanoriginatingsystem.rules.entity.Deviation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviationRepository extends JpaRepository<Deviation, String> {

    List<Deviation> findByApplicationIdAndStatus(String applicationId, String status);

    List<Deviation> findByStatus(String status);


    List<Deviation> findByApplicationIdAndRuleIdAndTargetId(
            String applicationId,
            String ruleId,
            String targetId
    );

    List<Deviation> findByApplicationIdAndTargetId(String applicationId, String targetId);

}