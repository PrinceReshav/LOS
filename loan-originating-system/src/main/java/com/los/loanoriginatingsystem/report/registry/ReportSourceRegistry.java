package com.los.loanoriginatingsystem.report.registry;

import com.los.loanoriginatingsystem.applicant.entity.LoanApplicant;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.lead.entity.Lead;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.report.enums.ReportSourceObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.ArrayList;
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

    public boolean isValidField(ReportSourceObject source, String fieldName) {
        return describeFields(source).contains(fieldName);
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
