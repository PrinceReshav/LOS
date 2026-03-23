package com.los.loanoriginatingsystem.banking.camanalysis.dto;

import lombok.Data;

@Data
public class MonthlyAnalysisDTO {

    private String monthDate;

    private String value;

    private Integer column;
}