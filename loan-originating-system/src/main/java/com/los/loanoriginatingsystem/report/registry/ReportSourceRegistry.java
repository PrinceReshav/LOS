package com.los.loanoriginatingsystem.report.registry;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.report.dto.FieldDescriptorDTO;
import com.los.loanoriginatingsystem.report.enums.ReportSourceObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportSourceRegistry {

    private static final Map<ReportSourceObject, Class<?>> SOURCE_TO_ENTITY =
            new EnumMap<>(ReportSourceObject.class);

    static {
        SOURCE_TO_ENTITY.put(ReportSourceObject.LEAD, Lead.class);
        SOURCE_TO_ENTITY.put(ReportSourceObject.LOAN_APPLICATION, LoanApplication.class);
        SOURCE_TO_ENTITY.put(ReportSourceObject.LOAN_APPLICANT, LoanApplicant.class);
        SOURCE_TO_ENTITY.put(ReportSourceObject.DOCUMENT, Document.class);
    }

    public Class<?> resolveEntityClass(ReportSourceObject source) {

        Class<?> clazz = SOURCE_TO_ENTITY.get(source);

        if (clazz == null) {
            throw new RuntimeException(
                    "Unsupported report source object : " + source
            );
        }

        return clazz;
    }

    // Every simple, reportable field on the object — used by the
    // report builder UI to populate available field pickers, and to
    // validate that a requested field is actually reportable before
    // it's ever used to build a query.
    public List<String> describeFields(ReportSourceObject source) {

        Class<?> clazz = resolveEntityClass(source);

        List<String> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {

            if (isReportable(field.getType())) {
                fields.add(field.getName());
            }
        }

        return fields;
    }

    // Richer field metadata — type category, plus enum values where
    // applicable — so the frontend can render the right control for
    // each field (text box, number input, date picker, or a
    // Salesforce-style picklist dropdown) instead of a free-text box
    // for everything.
    public List<FieldDescriptorDTO> describeFieldsDetailed(ReportSourceObject source) {

        Class<?> clazz = resolveEntityClass(source);

        List<FieldDescriptorDTO> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {

            if (!isReportable(field.getType())) {
                continue;
            }

            FieldDescriptorDTO dto = new FieldDescriptorDTO();

            dto.setName(field.getName());
            dto.setType(categorize(field.getType()));

            if (field.getType().isEnum()) {

                dto.setEnumValues(
                        Arrays.stream(field.getType().getEnumConstants())
                                .map(Object::toString)
                                .toList()
                );
            }

            fields.add(dto);
        }

        return fields;
    }

    public boolean isValidField(ReportSourceObject source, String fieldName) {
        return describeFields(source).contains(fieldName);
    }

    // Returns the actual Java type of a field — used to validate that
    // aggregate functions like SUM/AVG are only applied to numeric
    // fields, with a clean error instead of a runtime cast failure.
    public Class<?> getFieldType(ReportSourceObject source, String fieldName) {

        Class<?> clazz = resolveEntityClass(source);

        try {
            return clazz.getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(
                    "Unknown field '" + fieldName + "' for " + source
            );
        }
    }

    public boolean isNumericField(ReportSourceObject source, String fieldName) {
        return Number.class.isAssignableFrom(getFieldType(source, fieldName));
    }

    private String categorize(Class<?> type) {

        if (Number.class.isAssignableFrom(type) || type.isPrimitive() && type != boolean.class) {
            return "NUMBER";
        }

        if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            return "BOOLEAN";
        }

        if (Temporal.class.isAssignableFrom(type)) {
            return "DATE";
        }

        if (type.isEnum()) {
            return "PICKLIST";
        }

        return "STRING";
    }

    private boolean isReportable(Class<?> type) {

        return type.isPrimitive()
                || String.class.equals(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class.equals(type)
                || type.isEnum()
                || Temporal.class.isAssignableFrom(type);
    }
}
