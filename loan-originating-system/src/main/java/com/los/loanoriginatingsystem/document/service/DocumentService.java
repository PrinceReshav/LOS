package com.los.loanoriginatingsystem.document.service;

import com.los.loanoriginatingsystem.banking.camanalysis.repository.AverageBankBalanceRepository;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.event.DocumentProcessedEvent;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AverageBankBalanceRepository abbRepository;
    private final EventDispatcher eventDispatcher;

    public void updateDocument(Document doc) {

        Document oldDoc = documentRepository
                .findById(doc.getId())
                .orElseThrow();

        documentRepository.save(doc);

        // 🔥 EVENT CONDITION (Salesforce trigger equivalent)
        if ("Account Aggregator".equals(doc.getDocumentType())
                && "Processed".equals(doc.getBankStatementStatus())
                && !doc.getBankStatementStatus()
                .equals(oldDoc.getBankStatementStatus())) {

            eventDispatcher.dispatchAsync(
                    new DocumentProcessedEvent(doc.getId()),
                    new EventContext(Map.of(
                            "document", doc,
                            "applicationId", doc.getLoanApplicationId()
                    ))
            );
        }
    }

    public void deleteBankStatement(String documentId) {

        abbRepository.deleteByDocumentId(documentId);

        documentRepository.deleteById(documentId);
    }

    /*
     * only one bank statement is primary.
     */
    public void markPrimaryStatement(String documentId, String applicationId) {

        List<Document> documents =
                documentRepository.findByApplicationId(applicationId);

        for (Document doc : documents) {

            if (doc.getId().equals(documentId)) {

                doc.setBankStatementPrimary(true);

            } else if (Boolean.TRUE.equals(doc.getBankStatementPrimary())) {

                doc.setBankStatementPrimary(false);
            }
        }

        documentRepository.saveAll(documents);
    }
}