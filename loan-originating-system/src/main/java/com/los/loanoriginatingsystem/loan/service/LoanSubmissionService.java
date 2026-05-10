package com.los.loanoriginatingsystem.loan.service;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.applicant.repository.LoanApplicantRepository;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.document.validation.DocumentValidationService;
import com.los.loanoriginatingsystem.lead.service.LeadService;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import com.los.loanoriginatingsystem.temp.validation.TempLoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanSubmissionService {

    // =============================
    // 🔥 DEPENDENCIES
    // =============================
    private final TempLoanApplicationRepository tempRepo;
    private final LoanApplicationRepository loanRepo;
    private final LoanApplicantRepository applicantRepo;
    private final DocumentRepository documentRepo;
    private final LeadService leadService;
    private final TempLoanValidator validator;
    private final DocumentValidationService documentValidationService;
    private final LoanNumberService loanNumberService;

    // =============================
    // 🔥 MAIN SUBMIT
    // =============================
    @Transactional
    public String submit(String tempId) {

        // 🔒 LOCK
        TempLoanApplication temp = tempRepo.findByIdForUpdate(tempId)
                .orElseThrow(() -> new RuntimeException("Temp not found"));

        // 🚫 IDEMPOTENCY
        if (Boolean.TRUE.equals(temp.getIsSubmitted())) {
            return temp.getSubmissionRef(); // ✅ idempotent
            //throw new RuntimeException("Application already submitted");
        }

        // 🔍 VALIDATION
        validator.validateForSubmit(temp);
        documentValidationService.validateForSubmit(temp);

        // 🆔 LAN GENERATION
        String lan = loanNumberService.generateLAN();

        // 🏦 LOAN
        LoanApplication loan = new LoanApplication();
        loan.setId(UUID.randomUUID().toString());
        loan.setAccountNumber(lan);

        loanRepo.save(loan);

        // 👤 APPLICANT
        LoanApplicant applicant = buildApplicant(temp, loan.getId());
        applicantRepo.save(applicant);

        // 📄 DOCUMENT LINKING
        List<Document> docs = documentRepo.findByTempLoanId(tempId);

        for (Document doc : docs) {
            //doc.setTempLoanId(null);
            doc.setLoanApplicationId(loan.getId());

            if (doc.getKycType() != null) {
                doc.setLoanApplicantId(applicant.getId());
            }
        }

        documentRepo.saveAll(docs);

        // 🔐 MARK SUBMITTED
        temp.setIsSubmitted(true);
        temp.setSubmissionRef(lan);

        tempRepo.save(temp);

        // =============================
        // 🔗 UPDATE LEAD
        // =============================
        leadService.markConverted(temp.getLeadId(), loan.getId());

        return lan;
    }

    // =============================
    // 🔧 HELPER METHOD (FIX ERROR)
    // =============================
    private LoanApplicant buildApplicant(TempLoanApplication temp, String loanId) {

        LoanApplicant applicant = new LoanApplicant();

        applicant.setId(UUID.randomUUID().toString());
        applicant.setLoanApplicationId(loanId);

        // Example mapping (extend later)
        applicant.setAadhaarNumber(temp.getAadhaar().getVerifiedAadhaarNumber());
        applicant.setPanNumber(temp.getPan().getVerifiedPanNumber());
        applicant.setMobileNumber(temp.getMobile().getMobileNumber());

        return applicant;
    }
}