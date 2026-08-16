package com.los.loanoriginatingsystem.commercial.service;

import com.los.loanoriginatingsystem.commercial.entity.CommercialMatrix;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Builds the dynamic "which matrix rows apply to this loan" query as a
 * type-safe JPA Specification, instead of Salesforce's string-concatenated
 * dynamic SOQL (CommercialMatrixHandler.returnCommMatrixBased()). Each
 * predicate is "row's bound is null (no restriction) OR the loan's value
 * satisfies the bound".
 */
final class CommercialMatrixSpecifications {

    private CommercialMatrixSpecifications() {}

    static Specification<CommercialMatrix> matches(
            String scheme,
            String loanType,
            String securedLoanCategory,
            String productCode,
            Integer creditScore,
            BigDecimal loanAmount,
            BigDecimal total,
            BigDecimal processingFee
    ) {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("active")),
                cb.or(cb.isNull(root.get("scheme")), cb.equal(root.get("scheme"), scheme)),
                cb.or(cb.isNull(root.get("loanType")), cb.equal(root.get("loanType"), loanType)),
                cb.or(cb.isNull(root.get("securedLoanCategory")), cb.equal(root.get("securedLoanCategory"), securedLoanCategory)),
                cb.or(cb.isNull(root.get("productCode")), cb.equal(root.get("productCode"), productCode)),
                cb.or(cb.isNull(root.get("minCreditScore")), cb.le(root.<Integer>get("minCreditScore"), creditScore)),
                cb.or(cb.isNull(root.get("maxCreditScore")), cb.ge(root.<Integer>get("maxCreditScore"), creditScore)),
                cb.or(cb.isNull(root.get("minLoanAmount")), cb.le(root.<BigDecimal>get("minLoanAmount"), loanAmount)),
                cb.or(cb.isNull(root.get("maxLoanAmount")), cb.ge(root.<BigDecimal>get("maxLoanAmount"), loanAmount)),
                cb.or(cb.isNull(root.get("minTotal")), cb.le(root.<BigDecimal>get("minTotal"), total)),
                cb.or(cb.isNull(root.get("maxTotal")), cb.ge(root.<BigDecimal>get("maxTotal"), total)),
                cb.or(cb.isNull(root.get("minProcessingFee")), cb.le(root.<BigDecimal>get("minProcessingFee"), processingFee)),
                cb.or(cb.isNull(root.get("maxProcessingFee")), cb.ge(root.<BigDecimal>get("maxProcessingFee"), processingFee))
        );
    }
}
