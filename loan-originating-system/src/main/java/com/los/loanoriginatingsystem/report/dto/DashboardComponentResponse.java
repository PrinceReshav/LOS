package com.los.loanoriginatingsystem.report.dto;

import com.los.loanoriginatingsystem.report.enums.ChartType;
import com.los.loanoriginatingsystem.report.enums.DashboardComponentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardComponentResponse {

    private String id;
    private String reportId;
    private String title;
    private DashboardComponentType componentType;
    private ChartType chartType;
    private Integer positionRow;
    private Integer positionCol;
    private Integer width;
    private Integer height;

    // Populated only when fetched as part of full dashboard data.
    private ReportExecutionResult data;
}
