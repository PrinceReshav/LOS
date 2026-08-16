package com.los.loanoriginatingsystem.loan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectLoanRequest {

    @NotBlank(message = "remarks is mandatory when rejecting a loan application")
    private String remarks;

    /** Optional short code, e.g. "LOW_CREDIT_SCORE", "INCOMPLETE_DOCS" - free text if omitted. */
    private String rejectCode;
}
