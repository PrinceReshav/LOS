/*
*package com.los.loanoriginatingsystem.banking.bankstatement.service;

import com.los.loanoriginatingsystem.banking.camanalysis.entity.AverageBankBalance;
import com.los.loanoriginatingsystem.banking.camanalysis.repository.AverageBankBalanceRepository;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankStatementService {

    private final DocumentRepository documentRepository;
    private final AverageBankBalanceRepository abbRepository;

    public Object getBankStatements(String documentId) {

        Document doc =
                documentRepository.findById(documentId).orElseThrow();

        if (!"Processed".equals(doc.getBankStatementStatus())
                && !"Downloaded".equals(doc.getBankStatementStatus())) {

            return doc.getBankStatementStatus() + ","
                    + doc.getPreviewUrl();

        } else {

            return abbRepository.findByDocumentId(documentId)
                    .stream()
                    .findFirst()
                    .orElse(null);
        }
    }
}
*/