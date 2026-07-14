package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.dto.DocumentSummaryDTO;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentViewController {

    private final DocumentRepository repository;

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> view(
            @PathVariable String id
    ) {

        Document doc =
                repository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Document not found : " + id
                                )
                        );

        byte[] bytes =
                Base64.getDecoder()
                        .decode(doc.getFileData());

        MediaType mediaType =
                doc.getContentType() != null
                        ? MediaType.parseMediaType(doc.getContentType())
                        : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getFileName() + "\""
                )
                .body(bytes);
    }

    @GetMapping("/applicant/{applicantId}")
    public List<DocumentSummaryDTO> getApplicantDocuments(
            @PathVariable String applicantId
    ) {

        return repository
                .findByLoanApplicantId(applicantId)
                .stream()
                .map(DocumentViewController::toSummary)
                .toList();
    }

    // Application-level documents (e.g. Bank Statement) that aren't
    // tied to a specific applicant.
    @GetMapping("/application/{loanApplicationId}")
    public List<DocumentSummaryDTO> getApplicationDocuments(
            @PathVariable String loanApplicationId
    ) {

        return repository
                .findByLoanApplicationId(loanApplicationId)
                .stream()
                .map(DocumentViewController::toSummary)
                .toList();
    }

    private static DocumentSummaryDTO toSummary(Document doc) {

        return DocumentSummaryDTO.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .fileName(doc.getFileName())
                .contentType(doc.getContentType())
                .status(doc.getStatus())
                .verified(doc.getIsVerified())
                .build();
    }

}