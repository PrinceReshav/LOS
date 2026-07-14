package com.los.loanoriginatingsystem.temp.dto;

import com.los.loanoriginatingsystem.temp.enums.ApplicationStage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempApplicationResumeDTO {

    private String tempId;

    private String leadId;

    private Boolean isResume;

    private Boolean isSubmitted;

    private ApplicationStage currentStage;
}