package com.los.loanoriginatingsystem.commercial.dto;

import lombok.Data;

@Data
public class CommercialApprovalActionRequest {
    private String employeeId;
    private String comment;
    /** Which approver slot is acting: 1 or 2. */
    private Integer approverSlot;
}
