package com.los.loanoriginatingsystem.orchestration;

import com.los.loanoriginatingsystem.document.validation.DocumentValidationService;
import com.los.loanoriginatingsystem.outbox.service.OutboxService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.applicant.repository.LoanApplicantRepository;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.temp.validation.TempLoanValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanSubmissionOrchestrationService {

    private final TempLoanApplicationRepository tempRepo;
    private final LoanApplicationRepository loanRepo;
    private final LoanApplicantRepository applicantRepo;
    private final DocumentRepository documentRepo;
    private final TempLoanValidator  validator;
    private final DocumentValidationService documentValidationService;
    private final OutboxService outboxService;

    private static final Logger log = LoggerFactory.getLogger(LoanSubmissionOrchestrationService.class);

    @Transactional
    public String submit(String tempId) {


        // 🔒 LOCK (CRITICAL)
        TempLoanApplication temp = tempRepo.findByIdForUpdate(tempId)
                .orElseThrow(() -> new RuntimeException("Temp not found"));
        // =============================
        // 🔒 IDEMPOTENCY
        // =============================
        if (Boolean.TRUE.equals(temp.getIsSubmitted())) {
            return temp.getSubmissionRef();
        }

        // =============================
        // 🔍 VALIDATION
        // =============================
        validator.validateForSubmit(temp);
        documentValidationService.validateForSubmit(temp);

        // =============================
        // 🆔 SUBMISSION REF
        // =============================
        String submissionRef = UUID.randomUUID().toString();

        temp.setIsSubmitted(true);
        temp.setSubmissionRef(submissionRef);
        temp.setSubmissionStatus("INITIATED"); // optional but strong

        tempRepo.save(temp);

        // =============================
        // 📦 OUTBOX EVENT
        // =============================
        outboxService.saveEvent(
                "LOAN",
                tempId,
                "LOAN_SUBMIT",
                temp
        );
        log.info("Loan submission initiated tempId={} submissionRef={}",
                tempId, submissionRef);
        return submissionRef;
    }

}