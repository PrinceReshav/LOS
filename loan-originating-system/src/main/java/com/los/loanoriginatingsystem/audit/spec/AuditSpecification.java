package com.los.loanoriginatingsystem.audit.spec;

import com.los.loanoriginatingsystem.audit.entity.ActionAudit;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AuditSpecification {

    public static Specification<ActionAudit> filter(
            String entityType,
            String entityId,
            String action,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            predicates = cb.and(predicates,
                    cb.equal(root.get("entityType"), entityType));

            predicates = cb.and(predicates,
                    cb.equal(root.get("entityId"), entityId));

            if (action != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("action"), action));
            }

            if (from != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }

            if (to != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return predicates;
        };
    }
}