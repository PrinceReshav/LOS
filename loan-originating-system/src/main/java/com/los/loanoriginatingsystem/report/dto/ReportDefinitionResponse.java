package com.los.loanoriginatingsystem.report.dto;

import com.los.loanoriginatingsystem.report.enums.AggregateFunction;
import com.los.loanoriginatingsystem.report.enums.ChartType;
import com.los.loanoriginatingsystem.report.enums.ReportSourceObject;
import com.los.loanoriginatingsystem.report.enums.ReportType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReportDefinitionResponse {

    private String id;
    private String name;
    private String description;
    private String folderId;
    private ReportSourceObject sourceObject;
    private ReportType reportType;
    private List<String> selectedFields;
    private String groupByField1;
    private String groupByField2;
    private String aggregateField;
    private AggregateFunction aggregateFunction;
    private String sortField;
    private String sortDirection;
    private ChartType chartType;
    private List<ReportFilterCriteriaDTO> filters;
    private Boolean isStandard;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
