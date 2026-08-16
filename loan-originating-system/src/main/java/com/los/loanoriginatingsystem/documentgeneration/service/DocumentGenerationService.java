package com.los.loanoriginatingsystem.documentgeneration.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.documentgeneration.dto.GenerateDocumentRequest;
import com.los.loanoriginatingsystem.documentgeneration.dto.GenerateDocumentResponse;
import com.los.loanoriginatingsystem.documentgeneration.entity.DocumentTemplate;
import com.los.loanoriginatingsystem.documentgeneration.repository.DocumentTemplateRepository;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Generates a PDF document for a loan application from an admin-managed
 * DocumentTemplate: merge -> render -> store (reusing the existing
 * document.entity.Document table so generated docs show up through the
 * same /api/documents/view/{id} and checklist flows as uploaded ones).
 *
 * This is the Java equivalent of the old Salesforce
 * GenerateDocumentEsignController + its family of Visualforce document
 * pages, minus the one-page-per-document-type coupling: adding a new
 * document type here is a new DocumentTemplate row, not a new class/page.
 */
@Service
@RequiredArgsConstructor
public class DocumentGenerationService {

    private final DocumentTemplateRepository templateRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final DocumentRepository documentRepository;
    private final DocumentMergeModelBuilder mergeModelBuilder;
    private final PdfRenderer pdfRenderer;

    @Qualifier("documentTemplateEngine")
    private final TemplateEngine documentTemplateEngine;

    @Transactional
    public GenerateDocumentResponse generate(
            String loanApplicationId,
            String templateCode,
            GenerateDocumentRequest request
    ) {

        LoanApplication app = loanApplicationRepository.findById(loanApplicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found: " + loanApplicationId));

        DocumentTemplate template = templateRepository.findByCodeAndActiveTrue(templateCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active document template found for code: " + templateCode));

        Map<String, Object> model = mergeModelBuilder.build(app, request != null ? request.getExtraData() : null);

        Context context = new Context();
        context.setVariables(model);

        String renderedHtml = documentTemplateEngine.process(template.getHtmlContent(), context);

        byte[] pdfBytes = pdfRenderer.render(renderedHtml);
        String base64 = Base64.getEncoder().encodeToString(pdfBytes);
        String fileName = templateCode.toLowerCase() + "_" + app.getApplicationNumber() + ".pdf";

        // A regeneration replaces the previous version of the same document
        // type for this application, rather than accumulating duplicates.
        Document document = documentRepository
                .findByLoanApplicationIdAndDocumentType(loanApplicationId, templateCode)
                .orElseGet(Document::new);

        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
        }

        document.setLoanApplicationId(loanApplicationId);
        document.setDocumentType(templateCode);
        document.setFileName(fileName);
        document.setContentType("application/pdf");
        document.setFileData(base64);
        document.setStatus("GENERATED");
        document.setIsProcessed(true);

        Document saved = documentRepository.save(document);

        return new GenerateDocumentResponse(
                saved.getId(),
                saved.getDocumentType(),
                saved.getFileName(),
                "/api/documents/view/" + saved.getId()
        );
    }
}
