package com.los.loanoriginatingsystem.temp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageResponseDTO {

    private String tempId;
    private String currentStage;
    private String message;
}
