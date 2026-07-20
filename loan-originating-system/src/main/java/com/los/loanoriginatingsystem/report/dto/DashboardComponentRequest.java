package com.los.loanoriginatingsystem.report.dto;

import com.los.loanoriginatingsystem.report.enums.ChartType;
import com.los.loanoriginatingsystem.report.enums.DashboardComponentType;
import lombok.Data;

@Data
public class DashboardComponentRequest {

    // Only populated/used for layout updates on existing components;
    // ignored (and generated fresh) when creating new ones.
    private String id;

    private String reportId;
    private String title;
    private DashboardComponentType componentType;
    private ChartType chartType;
    private Integer positionRow;
    private Integer positionCol;
    private Integer width;
    private Integer height;
}
