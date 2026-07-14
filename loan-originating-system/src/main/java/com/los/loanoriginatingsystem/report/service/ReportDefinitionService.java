package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.dto.*;
import com.los.loanoriginatingsystem.report.entity.ReportDefinition;
import com.los.loanoriginatingsystem.report.entity.ReportFilterCriteria;
import com.los.loanoriginatingsystem.report.registry.ReportSourceRegistry;
import com.los.loanoriginatingsystem.report.repository.ReportDefinitionRepository;
import com.los.loanoriginatingsystem.report.repository.ReportFilterCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportDefinitionService {

    private final ReportDefinitionRepository repository;
    private final ReportFilterCriteriaRepository filterRepository;
    private final ReportSourceRegistry sourceRegistry;
    private final ReportQueryEngine queryEngine;

    public List<ReportDefinitionResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ReportDefinitionResponse getById(String id) {
        return toResponse(getEntity(id));
    }

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

        ReportDefinition report = getEntity(id);

        List<ReportFilterCriteria> filters =
                filterRepository.findByReportId(id);

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
    }

    private ReportDefinition getEntity(String id) {

        return repository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Report not found : " + id)
                );
    }

    private ReportDefinitionResponse toResponse(ReportDefinition report) {

        List<ReportFilterCriteriaDTO> filters =
                filterRepository.findByReportId(report.getId())
                        .stream()
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
