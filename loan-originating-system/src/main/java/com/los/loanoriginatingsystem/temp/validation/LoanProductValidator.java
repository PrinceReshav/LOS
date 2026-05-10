package com.los.loanoriginatingsystem.temp.validation;

import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import org.springframework.stereotype.Component;

@Component
public class LoanProductValidator {

    public void validate(TempLoanApplication temp) {

        var loan = temp.getLoanDetails();

        if (loan.getLoanType() == null) {
            throw new ValidationException("Loan Type is required");
        }

        if (loan.getLoanProductId() == null) {
            throw new ValidationException("Loan Product is required");
        }

        if (loan.getRequestedAmount() == null) {
            throw new ValidationException("Loan Amount is required");
        }

        if (loan.getTenureMonths() == null) {
            throw new ValidationException("Loan Tenure is required");
        }

        // 🔥 BUSINESS RULE: TENURE MULTIPLE OF 12
        if (loan.getTenureMonths() % 12 != 0) {
            throw new ValidationException("Tenure must be multiple of 12");
        }

        if (loan.getTenureMonths() > 72) {
            throw new ValidationException("Max tenure is 72 months");
        }

        // 🔥 BASIC AMOUNT VALIDATION (can be enhanced via LoanProduct entity later)
        if (loan.getRequestedAmount().intValue() <= 0) {
            throw new ValidationException("Invalid loan amount");
        }
    }
}