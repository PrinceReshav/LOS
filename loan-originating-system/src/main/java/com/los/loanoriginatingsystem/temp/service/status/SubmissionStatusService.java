package com.los.loanoriginatingsystem.temp.service.status;


import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.temp.dto.SubmissionStatusResponseDTO;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionStatusService {

    private final TempLoanApplicationRepository tempRepo;
    private final LoanApplicationRepository loanRepo;

    public SubmissionStatusResponseDTO getStatus(String submissionRef) {

        // =============================
        // 🔍 FETCH TEMP
        // =============================
        TempLoanApplication temp =
                tempRepo.findBySubmissionRef(submissionRef)
                        .orElseThrow(() -> new RuntimeException("Invalid submissionRef"));

        SubmissionStatusResponseDTO res = new SubmissionStatusResponseDTO();

        res.setSubmissionRef(submissionRef);
        res.setStatus(temp.getSubmissionStatus());

        // =============================
        // COMPLETED → fetch loan
        // =============================
        if ("COMPLETED".equals(temp.getSubmissionStatus())) {

            LoanApplication loan =
                    loanRepo.findByTempId(temp.getId())
                            .orElse(null);

            if (loan != null) {
                res.setLoanId(loan.getId());
            }

            res.setMessage("Loan created successfully");
        }

        // =============================
        // FAILED
        // =============================
        else if ("FAILED".equals(temp.getSubmissionStatus())) {

            res.setMessage(
                    temp.getFailureReason() != null
                            ? temp.getFailureReason()
                            : "Loan processing failed"
            );
        }

        // =============================
        // PROCESSING / INITIATED
        // =============================
        else {

            res.setMessage("Your application is being processed");
        }

        return res;
    }
}