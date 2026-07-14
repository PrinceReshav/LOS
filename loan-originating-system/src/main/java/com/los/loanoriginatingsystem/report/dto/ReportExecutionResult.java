package com.los.loanoriginatingsystem.report.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReportExecutionResult {

    private List<String> columns;
    private List<Map<String, Object>> rows;
    private boolean grouped;
    private int totalRows;
}
