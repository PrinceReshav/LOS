package com.los.administration.visibility.dto;

import com.los.administration.visibility.model.RecordAccessLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Everything RecordAccessService needs about the record being checked -
 * the calling service (e.g. loan-originating-system) supplies these
 * denormalized fields (owner/branch) itself rather than administration-service
 * reaching back across the network to fetch the record, since
 * administration-service has no business knowing LoanApplication's schema.
 */
@Data
public class RecordAccessCheckRequest {

    @NotBlank private String requestingUserId;
    @NotBlank private String recordType;
    @NotBlank private String recordId;

    /** userId of whoever created/owns the record (e.g. LoanApplication.createdBy). */
    private String ownerUserId;

    /** Branch the record belongs to (e.g. LoanApplication.branchId). */
    private String branchId;

    @NotNull private RecordAccessLevel requiredAccess;
}
