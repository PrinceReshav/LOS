package com.los.loanoriginatingsystem.banking.camanalysis.repository;

import com.los.loanoriginatingsystem.banking.camanalysis.entity.AverageBankBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AverageBankBalanceRepository
        extends JpaRepository<AverageBankBalance, Long> {

    List<AverageBankBalance> findByDocumentId(String documentId);

    void deleteByDocumentId(String documentId);

    List<AverageBankBalance> findByLoanApplicationIdAndSchemeType(
            String loanApplicationId,
            String schemeType
    );
}