package com.los.loanoriginatingsystem.document.service;

import com.los.loanoriginatingsystem.document.dto.DocumentVerificationResponse;

public interface DocumentVerificationService {

    DocumentVerificationResponse verify(
            String documentId
    );
}

/*
*package com.los.loanoriginatingsystem.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.kyc.mapper.KYCResponseMapper;
import com.los.loanoriginatingsystem.kyc.service.KYCOrchestrationService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DocumentVerificationService {

    private final DocumentRepository repository;
    private final KYCOrchestrationService kycService;
    private final TempLoanApplicationRepository tempRepo;
    private final KYCResponseMapper mapper;
    private final ObjectMapper  objectMapper;


    @Transactional
    public Object verifyDocument(String documentId) {

        Document doc = repository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (doc.getKycType() == null) {
            throw new RuntimeException("KYC type missing");
        }

        if (doc.getFileData() == null) {
            throw new RuntimeException("Document file data missing");
        }

        if (doc.getTempLoanId() == null) {
            throw new RuntimeException("TempLoanId missing for document");
        }

        TempLoanApplication temp = tempRepo.findById(doc.getTempLoanId())
                .orElseThrow(() -> new RuntimeException("Temp not found"));

        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(doc.getFileData());
        } catch (Exception e) {
            throw new RuntimeException("Invalid file format");
        }

        // 🔥 CALL KYC ENGINE
        Object response = kycService.process(
                doc.getKycType(),
                fileBytes
        );

        // 🔥 MAP RESPONSE → TEMP
        try {
            mapper.map(doc.getKycType(), response, temp);
        } catch (Exception e) {
            doc.setIsProcessed(true);
            doc.setIsVerified(false);
            repository.save(doc);
            throw e;
        }

        // 🔥 UPDATE DOCUMENT

        boolean success = response != null;


        doc.setIsProcessed(true);
        doc.setIsVerified(success);

        // doc.setKycResponse(response.toString()); // optional but recommended

        try {
            doc.setKycResponse(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            doc.setKycResponse("FAILED_TO_SERIALIZE");
        }
        repository.save(doc);
        tempRepo.save(temp);

        return response;
    }
}*/