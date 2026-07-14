package com.los.loanoriginatingsystem.temp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeTempApplicationResponseDTO {

    private String tempId;

    private String leadId;

    private Boolean resume;

    private String currentStage;

    private Boolean isKycCompleted;

    private Boolean isApplicantCompleted;

    private Boolean isSubmitted;
}