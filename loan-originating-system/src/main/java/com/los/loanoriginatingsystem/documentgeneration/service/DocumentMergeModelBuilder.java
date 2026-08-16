package com.los.loanoriginatingsystem.documentgeneration.service;

import com.los.loanoriginatingsystem.commercial.entity.CommercialApproval;
import com.los.loanoriginatingsystem.commercial.repository.CommercialApprovalRepository;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds the placeholder data model a DocumentTemplate is merged against.
 * Field names here are the exact variable names authors use in the stored
 * Thymeleaf template (e.g. th:text="${applicantName}").
 *
 * Deliberately flat (not nested objects) so template authors don't need to
 * know the underlying entity shape - every value is a plain
 * String/Number/BigDecimal ready to print.
 */
@Component
@RequiredArgsConstructor
public class DocumentMergeModelBuilder {

    private static final DateTimeFormatter GENERATED_ON_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final CommercialApprovalRepository commercialApprovalRepository;

    public Map<String, Object> build(LoanApplication app, Map<String, Object> extraData) {

        Map<String, Object> model = new HashMap<>();

        model.put("applicationNumber", app.getApplicationNumber());
        model.put("loanAccountNumber", app.getLoanAccountNumber());
        model.put("applicantName", app.getApplicantName());
        model.put("mobileNumber", app.getMobileNumber());
        model.put("email", app.getEmail());

        model.put("loanPurpose", app.getLoanPurpose());
        model.put("loanType", app.getLoanType());
        model.put("loanScheme", app.getLoanScheme());
        model.put("loanProductCode", app.getLoanProductCode());

        model.put("requestedAmount", app.getRequestedAmount());
        model.put("approvedAmount", app.getApprovedAmount());
        model.put("tenureMonths", app.getTenureMonths());
        model.put("roi", app.getRoi());
        model.put("emiAmount", app.getEmiAmount());

        model.put("branchName", app.getBranchName());
        model.put("branchCode", app.getBranchCode());

        model.put("bankName", app.getBankName());
        model.put("accountHolderName", app.getAccountHolderName());

        model.put("generatedOn", GENERATED_ON_FORMAT.format(java.time.LocalDateTime.now()));

        commercialApprovalRepository.findByLoanApplicationId(app.getId()).ifPresent(approval -> {
            model.put("commercialApprovalStatus", approval.getStatus());
            model.put("resolvedApprovalRole", approval.getResolvedRole());
        });

        if (extraData != null) {
            model.putAll(extraData); // caller-supplied values take precedence over defaults
        }

        return model;
    }
}
