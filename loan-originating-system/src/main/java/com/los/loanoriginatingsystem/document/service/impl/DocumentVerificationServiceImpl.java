package com.los.loanoriginatingsystem.document.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.loanoriginatingsystem.document.dto.DocumentVerificationResponse;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.document.service.DocumentVerificationService;
import com.los.loanoriginatingsystem.kyc.mapper.KYCResponseMapper;
import com.los.loanoriginatingsystem.kyc.service.KYCOrchestrationService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DocumentVerificationServiceImpl
        implements DocumentVerificationService {

    private final DocumentRepository documentRepo;

    private final TempLoanApplicationRepository tempRepo;

    private final KYCOrchestrationService kycService;

    private final KYCResponseMapper mapper;

    private final ObjectMapper objectMapper;

    @Override
    public DocumentVerificationResponse verify(
            String documentId
    ) {

        Document doc =
                documentRepo.findById(documentId)
                        .orElseThrow(
                                () -> new RuntimeException("Document not found")
                        );

        TempLoanApplication temp =
                tempRepo.findById(doc.getTempLoanId())
                        .orElseThrow(
                                () -> new RuntimeException("Temp not found")
                        );

        byte[] fileBytes;

        try {

            fileBytes =
                    Base64.getDecoder()
                            .decode(doc.getFileData());

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Invalid base64 document"
            );
        }

        Object response =
                kycService.process(
                        doc.getDocumentType(),
                        fileBytes
                );

        mapper.map(
                doc.getDocumentType(),
                response,
                temp
        );

        try {

            doc.setVerificationResponse(
                    objectMapper.writeValueAsString(
                            response
                    )
            );

        } catch (Exception ex) {

            doc.setVerificationResponse(
                    "FAILED_TO_SERIALIZE"
            );
        }

        doc.setIsProcessed(true);
        doc.setIsVerified(true);
        doc.setStatus("VERIFIED");

        documentRepo.save(doc);

        tempRepo.save(temp);

        return DocumentVerificationResponse
                .builder()
                .documentId(doc.getId())
                .verified(true)
                .message("Verification completed")
                .response(response)
                .build();
    }
}