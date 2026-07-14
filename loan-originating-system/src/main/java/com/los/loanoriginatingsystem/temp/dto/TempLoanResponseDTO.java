package com.los.loanoriginatingsystem.temp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempLoanResponseDTO {

    private String tempId;

    private String leadId;

    private String currentStage;

    private Boolean resume;
}