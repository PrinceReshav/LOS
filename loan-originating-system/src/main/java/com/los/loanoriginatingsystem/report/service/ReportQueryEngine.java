package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.dto.ReportExecutionResult;
import com.los.loanoriginatingsystem.report.entity.ReportDefinition;
import com.los.loanoriginatingsystem.report.entity.ReportFilterCriteria;
import com.los.loanoriginatingsystem.report.enums.FilterOperator;
import com.los.loanoriginatingsystem.report.registry.ReportSourceRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportQueryEngine {

    // Salesforce itself caps standard report execution at 2,000 rows
    // in-app (Data Export is the path for more) — this bounds both
    // memory use and response time the same way, rather than letting
    // a broad, unfiltered report try to load an entire table.
    private static final int MAX_ROWS = 2000;

    private final EntityManager entityManager;
    private final ReportSourceRegistry sourceRegistry;

    private static final int MAX_DISTINCT_VALUES = 200;

    // Real, currently-existing values for a field — used to populate
    // filter dropdowns with actual data instead of free-text boxes,
    // the same way Salesforce's report filter shows a picklist of
    // values that are actually in use.
    @SuppressWarnings("unchecked")
    public List<String> getDistinctValues(
            com.los.loanoriginatingsystem.report.enums.ReportSourceObject source,
            String field
    ) {

        Class<?> entityClass = sourceRegistry.resolveEntityClass(source);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Object> cq = (CriteriaQuery<Object>) (CriteriaQuery<?>) cb.createQuery();

        Root<?> root = cq.from(entityClass);

        cq.select((Selection<Object>) root.get(field)).distinct(true);

        List<Object> results =
                entityManager.createQuery(cq)
                        .setMaxResults(MAX_DISTINCT_VALUES)
                        .getResultList();

        return results.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .sorted()
                .toList();
    }

    public ReportExecutionResult execute(
            ReportDefinition report,
            List<ReportFilterCriteria> filters
    ) {

        Class<?> entityClass =
                sourceRegistry.resolveEntityClass(
                        report.getSourceObject()
                );

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> cq =
                cb.createTupleQuery();

        Root<?> root =
                cq.from(entityClass);

        boolean isGrouped =
                report.getGroupByField1() != null
                        && !report.getGroupByField1().isBlank();

        // ---------- SELECT clause ----------

        List<Selection<?>> selections = new ArrayList<>();
        List<String> columnLabels = new ArrayList<>();
        Expression<?> aggregateExpression = null;
        String aggregateLabel = null;

        if (isGrouped) {

            addGroupSelection(
                    root, cb, selections, columnLabels,
                    report.getGroupByField1()
            );

            if (
                    report.getGroupByField2() != null
                            && !report.getGroupByField2().isBlank()
            ) {

                addGroupSelection(
                        root, cb, selections, columnLabels,
                        report.getGroupByField2()
                );
            }

            if (report.getAggregateFunction() != null) {

                Expression<?> aggregate =
                        buildAggregate(
                                root, cb,
                                report.getAggregateFunction().name(),
                                report.getAggregateField()
                        );

                aggregateLabel =
                        report.getAggregateFunction().name()
                                + "_"
                                + (report.getAggregateField() == null
                                ? "id"
                                : report.getAggregateField());

                aggregateExpression = aggregate;

                selections.add(aggregate.alias(aggregateLabel));
                columnLabels.add(aggregateLabel);
            }

        } else {

            // Tabular: just the plain selected columns.
            List<String> fields =
                    report.getSelectedFields() == null
                            || report.getSelectedFields().isEmpty()
                            ? sourceRegistry.describeFields(
                            report.getSourceObject()
                    )
                            : report.getSelectedFields();

            for (String field : fields) {

                Path<?> path = root.get(field);

                selections.add(path.alias(field));

                columnLabels.add(field);
            }
        }

        cq.multiselect(selections);

        // ---------- WHERE clause ----------

        List<Predicate> predicates =
                buildPredicates(root, cb, filters);

        if (!predicates.isEmpty()) {
            cq.where(
                    cb.and(
                            predicates.toArray(new Predicate[0])
                    )
            );
        }

        // ---------- GROUP BY clause ----------

        if (isGrouped) {

            List<Expression<?>> groupFields = new ArrayList<>();

            groupFields.add(root.get(report.getGroupByField1()));

            if (
                    report.getGroupByField2() != null
                            && !report.getGroupByField2().isBlank()
            ) {
                groupFields.add(root.get(report.getGroupByField2()));
            }

            cq.groupBy(groupFields);
        }

        // ---------- ORDER BY clause ----------

        String sortField =
                report.getSortField() != null
                        && !report.getSortField().isBlank()
                        ? report.getSortField()
                        : (isGrouped
                        ? report.getGroupByField1()
                        : (columnLabels.isEmpty()
                        ? null
                        : columnLabels.get(0)));

        if (sortField != null && columnLabels.contains(sortField)) {

            boolean descending =
                    "DESC".equalsIgnoreCase(
                            report.getSortDirection()
                    );

            try {

                Expression<?> sortExpr =
                        sortField.equals(aggregateLabel)
                                ? aggregateExpression
                                : root.get(sortField);

                cq.orderBy(
                        descending
                                ? cb.desc(sortExpr)
                                : cb.asc(sortExpr)
                );

            } catch (Exception ignored) {
                // If the sort field isn't part of the group-by /
                // select projection, skip ordering rather than fail
                // the whole report.
            }
        }

        List<Tuple> tuples =
                entityManager.createQuery(cq)
                        .setMaxResults(MAX_ROWS + 1)
                        .getResultList();

        boolean truncated = tuples.size() > MAX_ROWS;

        if (truncated) {
            tuples = tuples.subList(0, MAX_ROWS);
        }

        List<Map<String, Object>> rows = new ArrayList<>();

        for (Tuple tuple : tuples) {

            Map<String, Object> row = new LinkedHashMap<>();

            for (String label : columnLabels) {

                Object value;

                try {
                    value = tuple.get(label);
                } catch (IllegalArgumentException e) {
                    value = null;
                }

                row.put(label, value);
            }

            rows.add(row);
        }

        ReportExecutionResult result = new ReportExecutionResult();

        result.setTruncated(truncated);

        result.setColumns(columnLabels);
        result.setRows(rows);
        result.setGrouped(isGrouped);
        result.setTotalRows(rows.size());

        return result;
    }

    private void addGroupSelection(
            Root<?> root,
            CriteriaBuilder cb,
            List<Selection<?>> selections,
            List<String> columnLabels,
            String field
    ) {

        Path<?> path = root.get(field);

        selections.add(path.alias(field));

        columnLabels.add(field);
    }

    private Expression<?> buildAggregate(
            Root<?> root,
            CriteriaBuilder cb,
            String function,
            String field
    ) {

        if ("COUNT".equals(function)) {

            Path<?> path =
                    field == null || field.isBlank()
                            ? root
                            : root.get(field);

            return cb.count(path);
        }

        Expression<Number> path =
                root.get(field).as(Number.class);

        return switch (function) {

            case "SUM" -> cb.sum(path);

            case "AVG" -> cb.avg(path);

            case "MIN" -> cb.min(path);

            case "MAX" -> cb.max(path);

            default -> cb.count(path);
        };
    }

    private List<Predicate> buildPredicates(
            Root<?> root,
            CriteriaBuilder cb,
            List<ReportFilterCriteria> filters
    ) {

        List<Predicate> predicates = new ArrayList<>();

        if (filters == null) {
            return predicates;
        }

        for (ReportFilterCriteria filter : filters) {

            Path<?> path =
                    root.get(filter.getFieldName());

            Class<?> fieldType =
                    path.getJavaType();

            Predicate predicate =
                    buildSinglePredicate(
                            cb, path, fieldType, filter
                    );

            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        return predicates;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildSinglePredicate(
            CriteriaBuilder cb,
            Path<?> path,
            Class<?> fieldType,
            ReportFilterCriteria filter
    ) {

        FilterOperator operator = filter.getOperator();

        if (operator == FilterOperator.IS_NULL) {
            return cb.isNull(path);
        }

        if (operator == FilterOperator.IS_NOT_NULL) {
            return cb.isNotNull(path);
        }

        if (operator == FilterOperator.CONTAINS) {
            return cb.like(
                    cb.lower(path.as(String.class)),
                    "%" + filter.getValue().toLowerCase() + "%"
            );
        }

        if (operator == FilterOperator.STARTS_WITH) {
            return cb.like(
                    cb.lower(path.as(String.class)),
                    filter.getValue().toLowerCase() + "%"
            );
        }

        if (operator == FilterOperator.IN) {

            List<Object> values = new ArrayList<>();

            for (String raw : filter.getValue().split(",")) {
                values.add(parseValue(fieldType, raw.trim()));
            }

            return path.in(values);
        }

        if (operator == FilterOperator.BETWEEN) {

            Comparable value1 =
                    (Comparable) parseValue(fieldType, filter.getValue());

            Comparable value2 =
                    (Comparable) parseValue(fieldType, filter.getValue2());

            return cb.between(
                    (Path<Comparable>) path,
                    value1,
                    value2
            );
        }

        Object value = parseValue(fieldType, filter.getValue());

        return switch (operator) {
            case EQUALS -> cb.equal(path, value);
            case NOT_EQUALS -> cb.notEqual(path, value);
            case GREATER_THAN -> cb.greaterThan(
                    (Path<Comparable>) path, (Comparable) value
            );
            case GREATER_OR_EQUAL -> cb.greaterThanOrEqualTo(
                    (Path<Comparable>) path, (Comparable) value
            );
            case LESS_THAN -> cb.lessThan(
                    (Path<Comparable>) path, (Comparable) value
            );
            case LESS_OR_EQUAL -> cb.lessThanOrEqualTo(
                    (Path<Comparable>) path, (Comparable) value
            );
            default -> cb.equal(path, value);
        };
    }

    // Best-effort conversion of a raw string filter value into the
    // actual Java type of the target entity field.
    private Object parseValue(Class<?> fieldType, String raw) {

        if (raw == null) {
            return null;
        }

        try {

            if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                return Boolean.parseBoolean(raw);
            }

            if (Long.class.equals(fieldType) || long.class.equals(fieldType)) {
                return Long.parseLong(raw);
            }

            if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
                return Integer.parseInt(raw);
            }

            if (Number.class.isAssignableFrom(fieldType)) {
                return new java.math.BigDecimal(raw);
            }

            if (LocalDate.class.equals(fieldType)) {
                return LocalDate.parse(raw);
            }

            if (LocalDateTime.class.equals(fieldType)) {
                return LocalDateTime.parse(raw);
            }

            if (fieldType.isEnum()) {
                return Enum.valueOf((Class<Enum>) fieldType, raw);
            }

        } catch (Exception e) {
            // Fall through to returning the raw string — lets the
            // caller get a clear DB-level type-mismatch error instead
            // of a silent misinterpretation.
        }

        return raw;
    }
}
