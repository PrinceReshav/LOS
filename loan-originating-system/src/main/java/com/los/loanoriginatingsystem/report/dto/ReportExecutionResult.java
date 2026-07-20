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

    // True when the report hit the 2,000 row execution cap and was
    // cut short — the frontend should surface this so users know to
    // narrow their filters rather than assume they've seen everything.
    private boolean truncated;
}
