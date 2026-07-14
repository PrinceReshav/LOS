/*package com.los.loanoriginatingsystem.banking.accountaggregator.service;
*
import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.banking.accountaggregator.dto.AACallbackDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.event.AACallbackEvent;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.integration.callback.service.CallbackReplayProtectionService;
import com.los.loanoriginatingsystem.integration.logging.service.ApiLogService;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountAggregatorCallbackService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final DocumentRepository documentRepository;
    private final ApiLogService apiLogService;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CallbackReplayProtectionService replayProtectionService;

    @Transactional
    public void handleCallback(AACallbackDTO wrapper) {

        if (wrapper.getRequestId() != null &&
                replayProtectionService.isReplay(wrapper.getRequestId())) {

            log.info("Duplicate AA callback ignored for requestId {}", wrapper.getRequestId());
            return;
        }

        Optional<LoanApplication> loanApplication =
                loanApplicationRepository
                        .findByAccountAggregatorFileNo(wrapper.getFileNo());

        if (loanApplication.isEmpty()) {
            log.warn("AA callback received but no loan found for fileNo {}", wrapper.getFileNo());
            return;
        }

        LoanApplication la = loanApplication.get();

        logCallback(wrapper, la);

        if ("Rejected".equals(wrapper.getStatus()) ||
                "Error".equals(wrapper.getStatus())) {

            la.setAccountAggregatorUrl("");
            la.setAccountAggregatorStatus(wrapper.getStatus());
            loanApplicationRepository.save(la);

            markReplay(wrapper);
            return;
        }

        la.setAccountAggregatorStatus(wrapper.getStatus());
        loanApplicationRepository.save(la);

        Document doc = fetchOrCreateDocument(wrapper, la);

        doc.setBankStatementDocId(wrapper.getDocId());
        doc.setBankStatementStatus(wrapper.getStatus());

        documentRepository.save(doc);

        if (isStatementReady(wrapper.getStatus())) {

            if (doc.getAaProcessing() == null || !doc.getAaProcessing()) {

                doc.setAaProcessing(true);
                documentRepository.save(doc);

                publishEvent(doc.getId());
            }
        }

        markReplay(wrapper);
    }

    private void markReplay(AACallbackDTO wrapper) {

        if (wrapper.getRequestId() != null) {
            replayProtectionService.markProcessed(wrapper.getRequestId());
        }
    }

    private Document fetchOrCreateDocument(AACallbackDTO wrapper, LoanApplication la) {

        Optional<Document> existing =
                documentRepository.findAccountAggregatorDocument(la.getId());

        if (existing.isPresent()) {
            return existing.get();
        }

        Document doc = new Document();
        doc.setApplicationId(la.getId());
        doc.setDocumentType("Account Aggregator");
        doc.setBankStatementDocId(wrapper.getDocId());
        doc.setBankStatementStatus(wrapper.getStatus());

        try {

            return documentRepository.save(doc);

        } catch (DataIntegrityViolationException ex) {

            log.info("AA document already created concurrently, fetching existing");

            return documentRepository
                    .findAccountAggregatorDocument(la.getId())
                    .orElseThrow();
        }
    }

    private void publishEvent(String documentId) {

        eventPublisher.publishEvent(
                new AACallbackEvent(documentId)
        );
    }

    private boolean isStatementReady(String status) {

        return "Processed".equals(status) ||
                "Downloaded".equals(status);
    }

    private void logCallback(AACallbackDTO wrapper, LoanApplication la) {

        try {

            apiLogService.log(
                    "Account Aggregator",
                    "",
                    objectMapper.writeValueAsString(wrapper),
                    la.getId()
            );

        } catch (Exception e) {

            log.error("Failed to serialize AA callback", e);

            apiLogService.log(
                    "Account Aggregator",
                    "",
                    wrapper.toString(),
                    la.getId()
            );
        }
    }
}
*/