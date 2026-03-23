package com.los.loanoriginatingsystem.banking.bankstatement.service;

import com.los.loanoriginatingsystem.banking.bankstatement.validation.BankStatementDuplicateValidator;
import com.los.loanoriginatingsystem.banking.camanalysis.entity.AverageBankBalance;
import com.los.loanoriginatingsystem.banking.camanalysis.repository.AverageBankBalanceRepository;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BankStatementUpdateService {

    private final DocumentRepository documentRepository;
    private final AverageBankBalanceRepository abbRepository;
    private final BankStatementDuplicateValidator duplicateValidator;

    public String updateBankStatements(
            String recordId,
            String rowsData,
            Boolean isPrimary,
            BigDecimal allAverage) {

        Optional<Document> docOptional = documentRepository.findById(recordId);

        if (docOptional.isPresent()) {
            return updateUsingDocument(docOptional.get(), rowsData, isPrimary, allAverage);
        }

        Optional<AverageBankBalance> abbOptional = findABB(recordId);

        if (abbOptional.isPresent()) {
            return updateUsingABB(abbOptional.get(), rowsData, isPrimary, allAverage);
        }

        throw new IllegalArgumentException("Invalid record id: " + recordId);
    }

    private Optional<AverageBankBalance> findABB(String recordId) {

        try {
            Long id = Long.valueOf(recordId);
            return abbRepository.findById(id);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private String updateUsingDocument(
            Document doc,
            String rowsData,
            Boolean isPrimary,
            BigDecimal allAverage) {

        // validate duplicate BEFORE saving
        duplicateValidator.validateDuplicateBankAccount(
                doc.getApplicationId(),
                doc.getAccountNumber(),
                doc.getId()
        );

        doc.setBankStatementStatus("Processed");

        if (Boolean.TRUE.equals(isPrimary)) {
            enforceSinglePrimary(doc.getApplicationId(), doc.getId());
        }

        doc.setBankStatementPrimary(isPrimary);
        documentRepository.save(doc);

        AverageBankBalance abb =
                abbRepository.findByDocumentId(doc.getId())
                        .stream()
                        .findFirst()
                        .orElseGet(AverageBankBalance::new);

        abb.setRowsData(rowsData);
        abb.setDocumentId(doc.getId());
        abb.setLoanApplicationId(doc.getApplicationId());
        abb.setSchemeType("Bank Statement");
        abb.setAverageBankBalance(allAverage);

        abbRepository.save(abb);

        return "Success";
    }

    private String updateUsingABB(
            AverageBankBalance abb,
            String rowsData,
            Boolean isPrimary,
            BigDecimal allAverage) {

        abb.setRowsData(rowsData);
        abb.setAverageBankBalance(allAverage);

        abbRepository.save(abb);

        if (Boolean.TRUE.equals(isPrimary)) {

            documentRepository.findById(abb.getDocumentId())
                    .ifPresent(doc -> {

                        enforceSinglePrimary(doc.getApplicationId(), doc.getId());

                        doc.setBankStatementPrimary(true);
                        documentRepository.save(doc);
                    });
        }

        return "Success";
    }

    private void enforceSinglePrimary(String applicationId, String currentDocId) {

        documentRepository.findByApplicationId(applicationId)
                .forEach(d -> {

                    if (!d.getId().equals(currentDocId)
                            && Boolean.TRUE.equals(d.getBankStatementPrimary())) {

                        d.setBankStatementPrimary(false);
                        documentRepository.save(d);
                    }
                });
    }
}