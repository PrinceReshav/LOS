package com.los.loanoriginatingsystem.document.service;

import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KYCDocumentService {

    private final DocumentRepository repository;

    public String uploadKycDocument(
            String tempId,
            String base64,
            String fileName,
            String kycType
    ) {

        Document doc = new Document();

        doc.setId(UUID.randomUUID().toString());

        doc.setTempLoanId(tempId);

        doc.setFileData(base64);
        doc.setFileName(fileName);

        doc.setKycType(kycType); // AADHAAR / PAN / DL / VOTER / LIVENESS
        doc.setDocumentType(mapToDocumentType(kycType));

        doc.setIsVerified(false);
        doc.setIsProcessed(false);

        repository.save(doc);

        return doc.getId();
    }
    private String mapToDocumentType(String kycType) {

        if (kycType == null) return "Other";

        return switch (kycType.toUpperCase()) {

            case "AADHAAR" -> "Aadhaar Front";
            case "PAN" -> "PAN Card";
            case "DL" -> "Driving License";
            case "VOTER" -> "Voter Id";
            case "LIVENESS" -> "Customer Photo";

            default -> "Other";
        };
    }
}