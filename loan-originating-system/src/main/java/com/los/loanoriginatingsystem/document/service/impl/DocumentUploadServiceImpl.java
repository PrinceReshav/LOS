package com.los.loanoriginatingsystem.document.service.impl;

import com.los.loanoriginatingsystem.document.dto.DocumentUploadRequest;
import com.los.loanoriginatingsystem.document.dto.DocumentUploadResponse;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.document.service.DocumentUploadService;
import com.los.loanoriginatingsystem.document.util.ContentTypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentUploadServiceImpl
        implements DocumentUploadService {

    private final DocumentRepository repository;

    @Override
    public DocumentUploadResponse upload(
            DocumentUploadRequest request
    ) {

        // If this document type was already uploaded for this temp
        // application, replace it rather than creating a duplicate row.
        Document document =
                repository.findByTempLoanIdAndDocumentType(
                                request.getTempId(),
                                request.getDocumentType()
                        )
                        .orElseGet(Document::new);

        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
        }

        document.setTempLoanId(
                request.getTempId()
        );

        document.setDocumentType(
                request.getDocumentType()
        );

        document.setFileName(
                request.getFileName()
        );

        document.setContentType(
                ContentTypeResolver.resolve(
                        request.getFileName()
                )
        );

        document.setFileData(
                request.getFileData()
        );

        document.setStatus("PENDING");

        document.setIsProcessed(false);
        document.setIsVerified(false);

        repository.save(document);

        return DocumentUploadResponse.builder()
                .documentId(document.getId())
                .status("SUCCESS")
                .message("Document uploaded successfully")
                .build();
    }
}