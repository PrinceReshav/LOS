package com.los.loanoriginatingsystem.systemconfig.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StampDutyConfigRequest {
    @NotBlank private String stateCode;
    @NotBlank private String stateName;
    @NotNull private BigDecimal stampDutyPercent;
    private BigDecimal flatFee;
    private String description;
}
