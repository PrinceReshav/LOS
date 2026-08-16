package com.los.loanoriginatingsystem.report.dto;

import com.los.loanoriginatingsystem.report.enums.FilterOperator;
import lombok.Data;

@Data
public class ReportFilterCriteriaDTO {
    private String id;
    private String fieldName;
    private FilterOperator operator;
    private String value;
    private String value2;
}
