package com.los.loanoriginatingsystem.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalMatrixRequest {

    @NotNull private Integer level;

    /** Role code, e.g. CBM/CCM/DBM/DCM/ZBM/ZCM/BH/DY_CEO/CEO/MD - see rules.enums.UserRole. */
    @NotBlank private String requiredRole;

    @NotNull private Integer sequence;

    private String deviationType;
}
