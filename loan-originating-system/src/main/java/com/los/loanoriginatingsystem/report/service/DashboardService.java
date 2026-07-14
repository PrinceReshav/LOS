package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.dto.*;
import com.los.loanoriginatingsystem.report.entity.Dashboard;
import com.los.loanoriginatingsystem.report.entity.DashboardComponent;
import com.los.loanoriginatingsystem.report.repository.DashboardComponentRepository;
import com.los.loanoriginatingsystem.report.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardComponentRepository componentRepository;
    private final ReportDefinitionService reportDefinitionService;

    public List<DashboardResponse> getAll() {

        return dashboardRepository.findAll()
                .stream()
                .map(d -> toResponse(d, false))
                .toList();
    }

    public DashboardResponse getById(String id) {
        return toResponse(getEntity(id), false);
    }

    // Full dashboard including each component's freshly executed
    // report data — this is what the "Reports & Dashboards" viewer
    // page calls to render the dashboard.
    public DashboardResponse getDashboardData(String id) {
        return toResponse(getEntity(id), true);
    }

    public DashboardResponse create(
            DashboardRequest request,
            String createdBy
    ) {

        Dashboard dashboard = new Dashboard();

        dashboard.setId(UUID.randomUUID().toString());
        dashboard.setName(request.getName());
        dashboard.setDescription(request.getDescription());
        dashboard.setFolderId(request.getFolderId());
        dashboard.setIsStandard(false);
        dashboard.setCreatedBy(createdBy);
        dashboard.setCreatedAt(LocalDateTime.now());

        dashboardRepository.save(dashboard);

        saveComponents(dashboard.getId(), request.getComponents());

        return toResponse(dashboard, false);
    }

    public DashboardResponse update(
            String id,
            DashboardRequest request
    ) {

        Dashboard dashboard = getEntity(id);

        if (Boolean.TRUE.equals(dashboard.getIsStandard())) {
            throw new RuntimeException(
                    "Standard dashboards cannot be edited"
            );
        }

        dashboard.setName(request.getName());
        dashboard.setDescription(request.getDescription());
        dashboard.setFolderId(request.getFolderId());

        dashboardRepository.save(dashboard);

        componentRepository.deleteByDashboardId(id);
        saveComponents(id, request.getComponents());

        return toResponse(dashboard, false);
    }

    public void delete(String id) {

        Dashboard dashboard = getEntity(id);

        if (Boolean.TRUE.equals(dashboard.getIsStandard())) {
            throw new RuntimeException(
                    "Standard dashboards cannot be deleted"
            );
        }

        componentRepository.deleteByDashboardId(id);
        dashboardRepository.delete(dashboard);
    }

    private void saveComponents(
            String dashboardId,
            List<DashboardComponentRequest> componentRequests
    ) {

        if (componentRequests == null) {
            return;
        }

        for (DashboardComponentRequest req : componentRequests) {

            DashboardComponent component = new DashboardComponent();

            component.setId(UUID.randomUUID().toString());
            component.setDashboardId(dashboardId);
            component.setReportId(req.getReportId());
            component.setTitle(req.getTitle());
            component.setComponentType(req.getComponentType());
            component.setChartType(req.getChartType());
            component.setPositionRow(req.getPositionRow());
            component.setPositionCol(req.getPositionCol());
            component.setWidth(req.getWidth());
            component.setHeight(req.getHeight());

            componentRepository.save(component);
        }
    }

    private Dashboard getEntity(String id) {

        return dashboardRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Dashboard not found : " + id)
                );
    }

    private DashboardResponse toResponse(
            Dashboard dashboard,
            boolean includeData
    ) {

        List<DashboardComponentResponse> components =
                componentRepository.findByDashboardId(dashboard.getId())
                        .stream()
                        .map(c -> toComponentResponse(c, includeData))
                        .toList();

        return DashboardResponse.builder()
                .id(dashboard.getId())
                .name(dashboard.getName())
                .description(dashboard.getDescription())
                .folderId(dashboard.getFolderId())
                .isStandard(dashboard.getIsStandard())
                .createdBy(dashboard.getCreatedBy())
                .createdAt(dashboard.getCreatedAt())
                .components(components)
                .build();
    }

    private DashboardComponentResponse toComponentResponse(
            DashboardComponent component,
            boolean includeData
    ) {

        ReportExecutionResult data = null;

        if (includeData) {

            try {
                data = reportDefinitionService.execute(
                        component.getReportId()
                );
            } catch (Exception e) {
                // Don't let one broken component take down the whole
                // dashboard — surface an empty result instead.
                data = new ReportExecutionResult();
            }
        }

        return DashboardComponentResponse.builder()
                .id(component.getId())
                .reportId(component.getReportId())
                .title(component.getTitle())
                .componentType(component.getComponentType())
                .chartType(component.getChartType())
                .positionRow(component.getPositionRow())
                .positionCol(component.getPositionCol())
                .width(component.getWidth())
                .height(component.getHeight())
                .data(data)
                .build();
    }
}
