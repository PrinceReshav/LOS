package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.dto.*;
import com.los.loanoriginatingsystem.report.entity.Dashboard;
import com.los.loanoriginatingsystem.report.entity.DashboardComponent;
import com.los.loanoriginatingsystem.report.repository.DashboardComponentRepository;
import com.los.loanoriginatingsystem.report.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardComponentRepository componentRepository;
    private final ReportDefinitionService reportDefinitionService;

    @Transactional(readOnly = true)
    public List<DashboardResponse> getAll() {

        List<Dashboard> dashboards = dashboardRepository.findAll();

        List<String> dashboardIds = dashboards.stream()
                .map(Dashboard::getId)
                .toList();

        // One query for every dashboard's components instead of one
        // query per dashboard (N+1) — keeps the Dashboards Home list
        // fast regardless of how many dashboards exist.
        Map<String, List<DashboardComponent>> componentsByDashboardId =
                componentRepository.findByDashboardIdIn(dashboardIds)
                        .stream()
                        .collect(Collectors.groupingBy(DashboardComponent::getDashboardId));

        return dashboards.stream()
                .map(d -> toResponse(
                        d,
                        false,
                        null,
                        componentsByDashboardId.getOrDefault(d.getId(), List.of())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getById(String id) {
        return toResponse(getEntity(id), false, null);
    }

    // Full dashboard including each component's freshly executed
    // report data — this is what the "Reports & Dashboards" viewer
    // page calls to render the dashboard. filterOverrides are the
    // dashboard-level filter values selected by the viewer; each
    // component only receives the ones that are valid fields for
    // its own report's source object.
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData(
            String id,
            Map<String, String> filterOverrides
    ) {
        return toResponse(getEntity(id), true, filterOverrides);
    }

    @Transactional
    public DashboardResponse create(
            DashboardRequest request,
            String createdBy
    ) {

        Dashboard dashboard = new Dashboard();

        dashboard.setId(UUID.randomUUID().toString());
        dashboard.setName(request.getName());
        dashboard.setDescription(request.getDescription());
        dashboard.setFolderId(request.getFolderId());
        dashboard.setDashboardFilterFields(
                request.getFilterFields() == null
                        ? new ArrayList<>()
                        : request.getFilterFields()
        );
        dashboard.setIsStandard(false);
        dashboard.setCreatedBy(createdBy);
        dashboard.setCreatedAt(LocalDateTime.now());

        dashboardRepository.save(dashboard);

        saveComponents(dashboard.getId(), request.getComponents());

        return toResponse(dashboard, false, null);
    }

    @Transactional
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
        dashboard.setDashboardFilterFields(
                request.getFilterFields() == null
                        ? new ArrayList<>()
                        : request.getFilterFields()
        );

        dashboardRepository.save(dashboard);

        componentRepository.deleteByDashboardId(id);
        saveComponents(id, request.getComponents());

        return toResponse(dashboard, false, null);
    }

    @Transactional
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

    // Re-arranges component positions/sizes only — used by the
    // frontend's drag-and-drop grid so a full save doesn't need to
    // resend every field of every component.
    @Transactional
    public DashboardResponse updateLayout(
            String id,
            List<DashboardComponentRequest> layout
    ) {

        Dashboard dashboard = getEntity(id);

        List<DashboardComponent> existing =
                componentRepository.findByDashboardId(id);

        for (DashboardComponentRequest positionUpdate : layout) {

            existing.stream()
                    .filter(c -> c.getId().equals(positionUpdate.getId()))
                    .findFirst()
                    .ifPresent(component -> {

                        component.setPositionRow(positionUpdate.getPositionRow());
                        component.setPositionCol(positionUpdate.getPositionCol());
                        component.setWidth(positionUpdate.getWidth());
                        component.setHeight(positionUpdate.getHeight());

                        componentRepository.save(component);
                    });
        }

        return toResponse(dashboard, false, null);
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
            boolean includeData,
            Map<String, String> filterOverrides
    ) {
        return toResponse(
                dashboard,
                includeData,
                filterOverrides,
                componentRepository.findByDashboardId(dashboard.getId())
        );
    }

    private DashboardResponse toResponse(
            Dashboard dashboard,
            boolean includeData,
            Map<String, String> filterOverrides,
            List<DashboardComponent> componentEntities
    ) {

        List<DashboardComponentResponse> components =
                componentEntities.stream()
                        .map(c -> toComponentResponse(c, includeData, filterOverrides))
                        .toList();

        return DashboardResponse.builder()
                .id(dashboard.getId())
                .name(dashboard.getName())
                .description(dashboard.getDescription())
                .folderId(dashboard.getFolderId())
                .isStandard(dashboard.getIsStandard())
                .createdBy(dashboard.getCreatedBy())
                .createdAt(dashboard.getCreatedAt())
                .filterFields(dashboard.getDashboardFilterFields())
                .components(components)
                .build();
    }

    private DashboardComponentResponse toComponentResponse(
            DashboardComponent component,
            boolean includeData,
            Map<String, String> filterOverrides
    ) {

        ReportExecutionResult data = null;

        if (includeData) {

            try {
                data = reportDefinitionService.execute(
                        component.getReportId(),
                        filterOverrides
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
