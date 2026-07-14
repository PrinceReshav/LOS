package com.los.loanoriginatingsystem.report.dto;

import com.los.loanoriginatingsystem.report.enums.ChartType;
import com.los.loanoriginatingsystem.report.enums.DashboardComponentType;
import lombok.Data;

@Data
public class DashboardComponentRequest {

    private String reportId;
    private String title;
    private DashboardComponentType componentType;
    private ChartType chartType;
    private Integer positionRow;
    private Integer positionCol;
    private Integer width;
    private Integer height;
}
