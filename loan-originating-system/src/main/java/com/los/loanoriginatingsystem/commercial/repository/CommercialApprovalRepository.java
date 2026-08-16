package com.los.loanoriginatingsystem.commercial.repository;

import com.los.loanoriginatingsystem.commercial.entity.CommercialApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommercialApprovalRepository extends JpaRepository<CommercialApproval, String> {
    Optional<CommercialApproval> findByLoanApplicationId(String loanApplicationId);
}
