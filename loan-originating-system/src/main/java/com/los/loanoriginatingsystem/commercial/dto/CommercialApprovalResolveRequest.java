package com.los.loanoriginatingsystem.commercial.dto;

import lombok.Data;

@Data
public class CommercialApprovalResolveRequest {
    /** Set true when the standard co-applicant requirement is being waived - forces escalation to Business Head. */
    private boolean coApplicantWaiver;
    /** Whether to also assign eligible branch employees into approver1/approver2 as part of resolution. */
    private boolean autoAssignApprovers = true;
}
