/*package com.los.loanoriginatingsystem.banking.camanalysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.banking.camanalysis.dto.BankAccountCAMData;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.banking.camanalysis.entity.AverageBankBalance;
import com.los.loanoriginatingsystem.banking.camanalysis.repository.AverageBankBalanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CamPersistenceService {

    private final AverageBankBalanceRepository abbRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final DocumentRepository documentRepository;

    private final ObjectMapper objectMapper;

    public void saveCamResults(
            String documentId,
            List<BankAccountCAMData> camDataList) throws Exception {

        List<AverageBankBalance> existing =
                abbRepository.findByDocumentId(documentId);

        if (!existing.isEmpty()) {
            return;
        }

        Document document =
                documentRepository.findById(documentId)
                        .orElseThrow();

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findById(document.getApplicationId())
                        .orElseThrow();

        BigDecimal totalAverage = BigDecimal.ZERO;

        for (BankAccountCAMData data : camDataList) {

            AverageBankBalance abb = new AverageBankBalance();

            abb.setLoanApplicationId(loanApplication.getId());
            abb.setDocumentId(documentId);
            abb.setSchemeType("Bank Statement");

            abb.setRowsData(
                    objectMapper.writeValueAsString(data)
            );

            BigDecimal avg = data.getAverage();

            if (avg != null) {

                abb.setAverageBankBalance(avg);

                totalAverage = totalAverage.add(avg);
            }

            abbRepository.save(abb);
        }

        BigDecimal current =
                loanApplication.getAverageBankBalance();

        if (current != null) {
            loanApplication.setAverageBankBalance(
                    current.add(totalAverage)
            );
        }
        else {
            loanApplication.setAverageBankBalance(
                    totalAverage
            );
        }

        loanApplicationRepository.save(loanApplication);
    }
}
*/