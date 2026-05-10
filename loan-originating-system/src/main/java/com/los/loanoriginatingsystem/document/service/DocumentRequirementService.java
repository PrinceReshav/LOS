package com.los.loanoriginatingsystem.document.service;

import com.los.loanoriginatingsystem.document.dto.RequiredDocumentResponseDTO;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.service.LoanProductService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentRequirementService {

    private final TempLoanApplicationRepository tempRepo;
    private final LoanProductService loanProductService;
    private final DocumentRepository documentRepository;

    public RequiredDocumentResponseDTO getRequiredDocuments(String tempId) {

        // =============================
        // 1. LOAD TEMP
        // =============================
        TempLoanApplication temp = tempRepo.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Temp not found"));

        var loan = temp.getLoanDetails();

        // =============================
        // 2. GET PRODUCT CONFIG
        // =============================
        LoanProduct product = loanProductService.getProductConfig(
                loan.getLoanProductCode(),
                loan.getLoanType(),
                loan.getLoanScheme()
        );

        // =============================
        // 3. FETCH REQUIRED DOCS
        // =============================
        List<String> applicantDocs =
                loanProductService.getApplicantRequiredDocs(product);

        List<String> applicationDocs =
                loanProductService.getApplicationRequiredDocs(product);

        // =============================
        // 4. FETCH UPLOADED DOCS
        // =============================
        List<Document> docs =
                documentRepository.findByTempLoanId(tempId);

        List<String> uploadedDocs =
                docs.stream()
                        .map(doc -> doc.getKycType() != null
                                ? doc.getKycType()
                                : doc.getDocumentType())
                        .collect(Collectors.toList());

        // =============================
        // 5. BUILD RESPONSE
        // =============================
        RequiredDocumentResponseDTO response =
                new RequiredDocumentResponseDTO();

        response.setApplicantDocuments(applicantDocs);
        response.setApplicationDocuments(applicationDocs);
        response.setAlreadyUploaded(uploadedDocs);

        return response;
    }
}