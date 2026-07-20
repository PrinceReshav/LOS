package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.dto.*;
import com.los.loanoriginatingsystem.report.entity.ReportDefinition;
import com.los.loanoriginatingsystem.report.entity.ReportFilterCriteria;
import com.los.loanoriginatingsystem.report.enums.FilterOperator;
import com.los.loanoriginatingsystem.report.registry.ReportSourceRegistry;
import com.los.loanoriginatingsystem.report.repository.ReportDefinitionRepository;
import com.los.loanoriginatingsystem.report.repository.ReportFilterCriteriaRepository;
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
public class ReportDefinitionService {

    private final ReportDefinitionRepository repository;
    private final ReportFilterCriteriaRepository filterRepository;
    private final ReportSourceRegistry sourceRegistry;
    private final ReportQueryEngine queryEngine;

    @Transactional(readOnly = true)
    public List<ReportDefinitionResponse> getAll() {

        List<ReportDefinition> reports = repository.findAll();

        List<String> reportIds = reports.stream()
                .map(ReportDefinition::getId)
                .toList();

        // Fetch every report's filters in a single query instead of
        // one query per report (N+1) — this is what keeps the
        // Reports Home list fast as the number of saved reports grows.
        Map<String, List<ReportFilterCriteria>> filtersByReportId =
                filterRepository.findByReportIdIn(reportIds)
                        .stream()
                        .collect(Collectors.groupingBy(ReportFilterCriteria::getReportId));

        return reports.stream()
                .map(report -> toResponse(
                        report,
                        filtersByReportId.getOrDefault(report.getId(), List.of())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportDefinitionResponse getById(String id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public ReportDefinitionResponse create(
            ReportDefinitionRequest request,
            String createdBy
    ) {

        validateFields(request);

        ReportDefinition report = new ReportDefinition();

        report.setId(UUID.randomUUID().toString());
        report.setCreatedAt(LocalDateTime.now());
        report.setCreatedBy(createdBy);
        report.setIsStandard(false);

        applyRequest(report, request);

        repository.save(report);

        saveFilters(report.getId(), request.getFilters());

        return toResponse(report);
    }

    @Transactional
    public ReportDefinitionResponse update(
            String id,
            ReportDefinitionRequest request
    ) {

        ReportDefinition report = getEntity(id);

        if (Boolean.TRUE.equals(report.getIsStandard())) {
            throw new RuntimeException(
                    "Standard reports cannot be edited. Clone it instead."
            );
        }

        validateFields(request);

        applyRequest(report, request);

        report.setUpdatedAt(LocalDateTime.now());

        repository.save(report);

        filterRepository.deleteByReportId(id);
        saveFilters(id, request.getFilters());

        return toResponse(report);
    }

    @Transactional
    public ReportDefinitionResponse clone(String id, String createdBy) {

        ReportDefinition original = getEntity(id);

        ReportDefinition copy = new ReportDefinition();

        copy.setId(UUID.randomUUID().toString());
        copy.setName(original.getName() + " (Copy)");
        copy.setDescription(original.getDescription());
        copy.setFolderId(original.getFolderId());
        copy.setSourceObject(original.getSourceObject());
        copy.setReportType(original.getReportType());
        copy.setSelectedFields(original.getSelectedFields());
        copy.setGroupByField1(original.getGroupByField1());
        copy.setGroupByField2(original.getGroupByField2());
        copy.setAggregateField(original.getAggregateField());
        copy.setAggregateFunction(original.getAggregateFunction());
        copy.setSortField(original.getSortField());
        copy.setSortDirection(original.getSortDirection());
        copy.setChartType(original.getChartType());
        copy.setIsStandard(false);
        copy.setCreatedBy(createdBy);
        copy.setCreatedAt(LocalDateTime.now());

        repository.save(copy);

        List<ReportFilterCriteria> originalFilters =
                filterRepository.findByReportId(id);

        for (ReportFilterCriteria f : originalFilters) {

            ReportFilterCriteria clone = new ReportFilterCriteria();

            clone.setId(UUID.randomUUID().toString());
            clone.setReportId(copy.getId());
            clone.setFieldName(f.getFieldName());
            clone.setOperator(f.getOperator());
            clone.setValue(f.getValue());
            clone.setValue2(f.getValue2());

            filterRepository.save(clone);
        }

        return toResponse(copy);
    }

    @Transactional
    public void delete(String id) {

        ReportDefinition report = getEntity(id);

        if (Boolean.TRUE.equals(report.getIsStandard())) {
            throw new RuntimeException(
                    "Standard reports cannot be deleted"
            );
        }

        filterRepository.deleteByReportId(id);
        repository.delete(report);
    }

    public ReportExecutionResult execute(String id) {
        return execute(id, null);
    }

    // Runs the saved report, optionally merging in dashboard-level
    // filter overrides. Overrides are silently skipped for fields
    // that don't exist on this report's source object, so a single
    // dashboard filter can safely apply across components backed by
    // different objects.
    public ReportExecutionResult execute(
            String id,
            Map<String, String> filterOverrides
    ) {

        ReportDefinition report = getEntity(id);

        List<ReportFilterCriteria> filters =
                new ArrayList<>(
                        filterRepository.findByReportId(id)
                );

        if (filterOverrides != null) {

            for (Map.Entry<String, String> entry : filterOverrides.entrySet()) {

                String field = entry.getKey();
                String value = entry.getValue();

                if (value == null || value.isBlank()) {
                    continue;
                }

                if (!sourceRegistry.isValidField(report.getSourceObject(), field)) {
                    continue;
                }

                ReportFilterCriteria override = new ReportFilterCriteria();

                override.setFieldName(field);
                override.setOperator(FilterOperator.EQUALS);
                override.setValue(value);

                filters.add(override);
            }
        }

        return queryEngine.execute(report, filters);
    }

    // Runs a report definition that hasn't been saved yet — powers
    // the "Preview" button while building a report.
    public ReportExecutionResult executeAdhoc(
            ReportDefinitionRequest request
    ) {

        validateFields(request);

        ReportDefinition transientReport = new ReportDefinition();
        applyRequest(transientReport, request);

        List<ReportFilterCriteria> filters =
                request.getFilters()
                        .stream()
                        .map(dto -> {
                            ReportFilterCriteria f = new ReportFilterCriteria();
                            f.setFieldName(dto.getFieldName());
                            f.setOperator(dto.getOperator());
                            f.setValue(dto.getValue());
                            f.setValue2(dto.getValue2());
                            return f;
                        })
                        .toList();

        return queryEngine.execute(transientReport, filters);
    }

    private void applyRequest(
            ReportDefinition report,
            ReportDefinitionRequest request
    ) {

        report.setName(request.getName());
        report.setDescription(request.getDescription());
        report.setFolderId(request.getFolderId());
        report.setSourceObject(request.getSourceObject());
        report.setReportType(request.getReportType());
        report.setSelectedFields(request.getSelectedFields());
        report.setGroupByField1(request.getGroupByField1());
        report.setGroupByField2(request.getGroupByField2());
        report.setAggregateField(request.getAggregateField());
        report.setAggregateFunction(request.getAggregateFunction());
        report.setSortField(request.getSortField());
        report.setSortDirection(request.getSortDirection());
        report.setChartType(request.getChartType());
    }

    private void saveFilters(
            String reportId,
            List<ReportFilterCriteriaDTO> filterDTOs
    ) {

        if (filterDTOs == null) {
            return;
        }

        for (ReportFilterCriteriaDTO dto : filterDTOs) {

            ReportFilterCriteria filter = new ReportFilterCriteria();

            filter.setId(UUID.randomUUID().toString());
            filter.setReportId(reportId);
            filter.setFieldName(dto.getFieldName());
            filter.setOperator(dto.getOperator());
            filter.setValue(dto.getValue());
            filter.setValue2(dto.getValue2());

            filterRepository.save(filter);
        }
    }

    private void validateFields(ReportDefinitionRequest request) {

        if (request.getSourceObject() == null) {
            throw new RuntimeException("A report source object is required");
        }

        for (String field : request.getSelectedFields()) {
            if (!sourceRegistry.isValidField(request.getSourceObject(), field)) {
                throw new RuntimeException(
                        "Invalid field '" + field + "' for "
                                + request.getSourceObject()
                );
            }
        }

        if (
                request.getGroupByField1() != null
                        && !request.getGroupByField1().isBlank()
                        && !sourceRegistry.isValidField(
                        request.getSourceObject(),
                        request.getGroupByField1()
                )
        ) {
            throw new RuntimeException(
                    "Invalid group-by field : " + request.getGroupByField1()
            );
        }

        if (
                request.getGroupByField2() != null
                        && !request.getGroupByField2().isBlank()
                        && !sourceRegistry.isValidField(
                        request.getSourceObject(),
                        request.getGroupByField2()
                )
        ) {
            throw new RuntimeException(
                    "Invalid group-by field : " + request.getGroupByField2()
            );
        }

        if (
                request.getAggregateFunction() != null
                        && request.getAggregateField() != null
                        && !request.getAggregateField().isBlank()
        ) {

            if (!sourceRegistry.isValidField(request.getSourceObject(), request.getAggregateField())) {
                throw new RuntimeException(
                        "Invalid aggregate field : " + request.getAggregateField()
                );
            }

            boolean requiresNumeric =
                    switch (request.getAggregateFunction()) {
                        case SUM, AVG, MIN, MAX -> true;
                        default -> false;
                    };

            if (
                    requiresNumeric
                            && !sourceRegistry.isNumericField(
                            request.getSourceObject(),
                            request.getAggregateField()
                    )
            ) {
                throw new RuntimeException(
                        request.getAggregateFunction() + " requires a numeric field, but '"
                                + request.getAggregateField() + "' is not numeric"
                );
            }
        }
    }

    private ReportDefinition getEntity(String id) {

        return repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Report not found : " + id)
                );
    }

    private ReportDefinitionResponse toResponse(ReportDefinition report) {
        return toResponse(report, filterRepository.findByReportId(report.getId()));
    }

    private ReportDefinitionResponse toResponse(
            ReportDefinition report,
            List<ReportFilterCriteria> filterEntities
    ) {

        List<ReportFilterCriteriaDTO> filters =
                filterEntities.stream()
                        .map(f -> {
                            ReportFilterCriteriaDTO dto = new ReportFilterCriteriaDTO();
                            dto.setId(f.getId());
                            dto.setFieldName(f.getFieldName());
                            dto.setOperator(f.getOperator());
                            dto.setValue(f.getValue());
                            dto.setValue2(f.getValue2());
                            return dto;
                        })
                        .toList();

        return ReportDefinitionResponse.builder()
                .id(report.getId())
                .name(report.getName())
                .description(report.getDescription())
                .folderId(report.getFolderId())
                .sourceObject(report.getSourceObject())
                .reportType(report.getReportType())
                .selectedFields(report.getSelectedFields())
                .groupByField1(report.getGroupByField1())
                .groupByField2(report.getGroupByField2())
                .aggregateField(report.getAggregateField())
                .aggregateFunction(report.getAggregateFunction())
                .sortField(report.getSortField())
                .sortDirection(report.getSortDirection())
                .chartType(report.getChartType())
                .filters(filters)
                .isStandard(report.getIsStandard())
                .createdBy(report.getCreatedBy())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
