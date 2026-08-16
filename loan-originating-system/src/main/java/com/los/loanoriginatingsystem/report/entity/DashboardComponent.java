package com.los.loanoriginatingsystem.report.entity;

import com.los.loanoriginatingsystem.report.enums.ChartType;
import com.los.loanoriginatingsystem.report.enums.DashboardComponentType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "dashboard_component",
        indexes = @Index(name = "idx_dashboard_component_dashboard_id", columnList = "dashboardId")
)
@Data
public class DashboardComponent {

    @Id
    private String id;

    private String dashboardId;

    private String reportId;

    private String title;

    @Enumerated(EnumType.STRING)
    private DashboardComponentType componentType;

    @Enumerated(EnumType.STRING)
    private ChartType chartType;

    // Simple grid layout, top-left origin, in grid units.
    private Integer positionRow;
    private Integer positionCol;
    private Integer width;
    private Integer height;
}
