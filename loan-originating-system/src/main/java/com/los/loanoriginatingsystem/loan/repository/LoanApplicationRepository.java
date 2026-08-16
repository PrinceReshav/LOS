package com.los.loanoriginatingsystem.loan.repository;

import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository
        extends JpaRepository<LoanApplication, String> {

    Optional<LoanApplication> findByAccountAggregatorFileNo(String fileNo);
    boolean existsByTempId(String tempId);

    Optional<LoanApplication> findByTempId(String tempId);

    List<LoanApplication> findByCreatedByInOrBranchIdInOrIdIn(
            java.util.Collection<String> ownerUserIds,
            java.util.Collection<String> branchIds,
            java.util.Collection<String> recordIds
    );
}