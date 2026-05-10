package com.los.loanoriginatingsystem.document.validation;

import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.service.LoanProductService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.validation.ValidationException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentValidationService {

    private final DocumentRepository documentRepository;
    private final LoanProductService loanProductService;

    // =====================================================
    // 🔥 MAIN VALIDATION ENTRY
    // =====================================================
    public void validateForSubmit(TempLoanApplication temp) {

        // =============================
        // 1. FETCH PRODUCT CONFIG
        // =============================
        var loan = temp.getLoanDetails();

        LoanProduct product = loanProductService.getProductConfig(
                loan.getLoanProductCode(),
                loan.getLoanType(),
                loan.getLoanScheme()
        );

        // =============================
        // 2. GET REQUIRED DOCS
        // =============================
        List<String> requiredApplicantDocs =
                loanProductService.getApplicantRequiredDocs(product);

        List<String> requiredApplicationDocs =
                loanProductService.getApplicationRequiredDocs(product);

        // =============================
        // 3. FETCH UPLOADED DOCS
        // =============================
        List<Document> docs =
                documentRepository.findByTempLoanId(temp.getId());


// Below is a block that is faster for lookup O(1) but not necessary to do it for temp stages, max 10 documents.
// ~ 15 docs × 10 checks = 150 operations   That is nothing for a backend system.
//optimize only when needed 👉 Rule: “Don’t optimize prematurely”

/*
*Set<String> uploadedDocTypes =
*
*                docs.stream()
*                        .map(doc -> doc.getKycType() != null
*                                ? doc.getKycType()
*                                : doc.getDocumentType())
*                        .collect(Collectors.toSet());
*/

        // =============================
        // 4. VALIDATE APPLICATION DOCS
        // =============================
        for (String requiredDoc : requiredApplicationDocs) {

            boolean exists = docs.stream()
                    .anyMatch(d ->
                            requiredDoc.equals(d.getDocumentType())
                    );

            if (!exists) {
                throw new ValidationException(
                        "Missing required application document: " + requiredDoc
                );
            }
        }

        // =============================
        // 5. VALIDATE APPLICANT DOCS
        // =============================
        for (String requiredDoc : requiredApplicantDocs) {

            boolean exists = docs.stream()
                    .anyMatch(d ->
                            requiredDoc.equals(
                                    d.getKycType() != null ? d.getKycType() : d.getDocumentType()
                            )
                                    && Boolean.TRUE.equals(d.getIsVerified())
                    );

            if (!exists) {
                throw new ValidationException(
                        "Missing or unverified applicant document: " + requiredDoc
                );
            }
        }

        // =============================
        // 6. KYC VALIDATION (🔥 IMPORTANT)
        // =============================
        validateKyc(temp);
    }

    // =====================================================
    // 🔐 KYC VALIDATION
    // =====================================================
    private void validateKyc(TempLoanApplication temp) {

        // Aadhaar
        if (Boolean.FALSE.equals(temp.getAadhaar().getVerified())) {
            throw new ValidationException("Aadhaar not verified");
        }

        // PAN
        if (Boolean.FALSE.equals(temp.getPan().getVerified())) {
            throw new ValidationException("PAN not verified");
        }

        // Liveness
        if (Boolean.FALSE.equals(temp.getLiveness().getVerified())) {
            throw new ValidationException("Customer photo (liveness) not verified");
        }

        // Mobile
        if (Boolean.FALSE.equals(temp.getMobile().getMobileVerified())) {
            throw new ValidationException("Mobile not verified");
        }
    }
}