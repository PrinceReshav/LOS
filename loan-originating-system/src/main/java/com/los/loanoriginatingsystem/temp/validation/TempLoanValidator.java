package com.los.loanoriginatingsystem.temp.validation;

import com.los.loanoriginatingsystem.loan.config.entity.LoanEligibilityConfig;
import com.los.loanoriginatingsystem.loan.config.service.LoanConfigService;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class TempLoanValidator {

    private final LoanProductValidator loanValidator;
    private final KycValidator kycValidator;
    private final ApplicantValidator applicantValidator;
    private final LoanConfigService configService;
    // =============================
    // 🔍 FINAL SUBMIT VALIDATION
    // =============================


    public void validateForSubmit(@NonNull TempLoanApplication temp) {

        // Step 1 → Loan validation (config-driven)
        validateLoan(temp);

        // Step 2 → KYC validation
        kycValidator.validate(temp);

        // Step 3 → Applicant validation
        applicantValidator.validate(temp);

        // Step 4 → final flag
        if (!Boolean.TRUE.equals(temp.getIsApplicantCompleted())) {
            throw new ValidationException("Applicant details not completed");
        }
    }

    private void validateLoan(@NonNull TempLoanApplication temp) {

        var loan = temp.getLoanDetails();

        LoanEligibilityConfig config =
                configService.findMatchingConfig(
                        loan.getLoanProductCode(),
                        loan.getLoanType(),
                        loan.getLoanScheme(),
                        loan.getRequestedAmount(),
                        loan.getTenureMonths(),
                        temp.getApplicant().getCreditScore() == null
                                ? 700
                                : temp.getApplicant().getCreditScore()
                );

        if (config == null) {
            throw new ValidationException("Loan not eligible as per policy");
        }
    }

}