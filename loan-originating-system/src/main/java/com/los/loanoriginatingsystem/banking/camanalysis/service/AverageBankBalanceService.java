package com.los.loanoriginatingsystem.banking.camanalysis.service;

import com.los.loanoriginatingsystem.banking.camanalysis.entity.AverageBankBalance;
import com.los.loanoriginatingsystem.banking.camanalysis.repository.AverageBankBalanceRepository;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AverageBankBalanceService {

    private final AverageBankBalanceRepository abbRepository;
    private final LoanApplicationRepository loanRepository;

    public AverageBankBalance updateOrSave(
            String applicationId,
            String rowsData,
            BigDecimal averageBalance,
            String schemeType) {

        List<AverageBankBalance> list =
                abbRepository.findByLoanApplicationIdAndSchemeType(
                        applicationId,
                        schemeType
                );

        AverageBankBalance abb;

        if (list.isEmpty()) {

            abb = new AverageBankBalance();
            abb.setLoanApplicationId(applicationId);
            abb.setRowsData(rowsData);
            abb.setSchemeType(schemeType);

        } else {

            abb = list.get(0);
            abb.setRowsData(rowsData);
        }

        abbRepository.save(abb);

        LoanApplication la =
                loanRepository.findById(applicationId).orElseThrow();

        if ("Bank Details".equals(schemeType)) {

            la.setAverageBankBalance(averageBalance);

        } else if ("GST".equals(schemeType)) {

            la.setAnnualisedTurnover(averageBalance);
        }

        loanRepository.save(la);

        return abb;
    }
}