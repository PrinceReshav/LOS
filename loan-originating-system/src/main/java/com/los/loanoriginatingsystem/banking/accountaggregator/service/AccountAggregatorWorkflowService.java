package com.los.loanoriginatingsystem.banking.accountaggregator.service;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;
import com.los.loanoriginatingsystem.banking.camanalysis.service.CamAnalysisService;
import com.los.loanoriginatingsystem.banking.camanalysis.service.CamPersistenceService;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountAggregatorWorkflowService {

    private final AccountAggregatorService accountAggregatorService;
    private final CamAnalysisService camAnalysisService;
    private final CamPersistenceService camPersistenceService;
    private final DocumentRepository documentRepository;
    private final BankStatementFileService bankStatementFileService;

    @Async
    @Transactional
    public void processBankStatementAsync(String documentId) {

        // Try to acquire lock
        int updated = documentRepository.lockForProcessing(documentId);

        if (updated == 0) {
            log.info("AA callback already processed or in progress for {}", documentId);
            return;
        }

        try {

            log.info("Processing bank statement {}", documentId);

            BankStatementResponseDTO response =
                    accountAggregatorService.downloadBankStatement(documentId);

            var camResults =
                    camAnalysisService.processBankStatement(response);

            camPersistenceService.saveCamResults(
                    documentId,
                    camResults
            );

            bankStatementFileService.downloadBankStatementExcel(
                    documentId,
                    response.getDocId()
            );

            Document doc =
                    documentRepository.findById(documentId).orElseThrow();

            doc.setAaProcessing(false);

            documentRepository.save(doc);

        } catch (Exception e) {

            log.error("AA processing failed for document {}", documentId, e);

            documentRepository.findById(documentId)
                    .ifPresent(d -> {
                        d.setAaProcessing(false);
                        documentRepository.save(d);
                    });
        }
    }
}