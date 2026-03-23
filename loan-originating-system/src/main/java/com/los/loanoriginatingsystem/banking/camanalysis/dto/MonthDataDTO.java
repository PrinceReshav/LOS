package com.los.loanoriginatingsystem.banking.camanalysis.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MonthDataDTO {

    private String month;

    private BigDecimal average;

    private Integer row;

    private BigDecimal total;

    private List<MonthlyAnalysisDTO> columns;
}