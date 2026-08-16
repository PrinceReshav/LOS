package com.los.loanoriginatingsystem.loanProduct.service;

import com.los.loanoriginatingsystem.common.exception.ResourceNotFoundException;
import com.los.loanoriginatingsystem.loanProduct.dto.LoanProductDTO;
import com.los.loanoriginatingsystem.loanProduct.dto.LoanProductRequest;
import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanScheme;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanType;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.ProductCode;
import com.los.loanoriginatingsystem.loanProduct.repository.LoanProductRepository;
import com.los.loanoriginatingsystem.loanScheme.service.LoanSchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanProductService {

    private final LoanProductRepository repository;
    private final LoanSchemeService loanSchemeService;

    // =====================================================
    // 🔹 ADMIN CRUD + ACTIVATION
    // =====================================================

    @Transactional
    public LoanProduct create(LoanProductRequest request) {

        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("A loan product already exists with name: " + request.getName());
        }
        loanSchemeService.assertActive(request.getLoanScheme().name());
        validateAmountRange(request);

        LoanProduct entity = new LoanProduct();
        entity.setId(UUID.randomUUID().toString());
        entity.setIsActive(true);
        apply(entity, request);

        return repository.save(entity);
    }

    @Transactional
    public LoanProduct update(String id, LoanProductRequest request) {

        LoanProduct entity = getEntityOrThrow(id);
        loanSchemeService.assertActive(request.getLoanScheme().name());
        validateAmountRange(request);

        apply(entity, request);
        return repository.save(entity);
    }

    @Transactional
    public LoanProduct setActive(String id, boolean active) {
        LoanProduct entity = getEntityOrThrow(id);
        entity.setIsActive(active);
        return repository.save(entity);
    }

    public LoanProduct getById(String id) {
        return getEntityOrThrow(id);
    }

    public List<LoanProduct> getAll() {
        return repository.findAll();
    }

    private void validateAmountRange(LoanProductRequest request) {
        if (request.getMinLoanAmount() != null && request.getMaxLoanAmount() != null
                && request.getMinLoanAmount().compareTo(request.getMaxLoanAmount()) > 0) {
            throw new IllegalArgumentException("minLoanAmount cannot be greater than maxLoanAmount");
        }
    }

    private void apply(LoanProduct entity, LoanProductRequest request) {
        entity.setName(request.getName());
        entity.setLoanType(request.getLoanType());
        entity.setLoanScheme(request.getLoanScheme());
        entity.setProductCode(request.getProductCode());
        entity.setSecuredLoanCategory(request.getSecuredLoanCategory());
        entity.setCommercialType(request.getCommercialType());
        entity.setMinLoanAmount(request.getMinLoanAmount());
        entity.setMaxLoanAmount(request.getMaxLoanAmount());
        entity.setMinimumAgeBorrower(request.getMinimumAgeBorrower());
        entity.setFixedInterestRate(request.getFixedInterestRate());
        entity.setFixedProcessingFees(request.getFixedProcessingFees());
        entity.setMaxProcessingFees(request.getMaxProcessingFees());
        entity.setInsurancePercent(request.getInsurancePercent());
        entity.setApplicantDocuments(request.getApplicantDocuments());
        entity.setApplicationDocuments(request.getApplicationDocuments());
        entity.setLmsProductId(request.getLmsProductId());
        entity.setLmsPurposeCategoryId(request.getLmsPurposeCategoryId());
        entity.setPurposeCodeId(request.getPurposeCodeId());
        entity.setApprovalRequired(request.getApprovalRequired());
        entity.setCommercialData(request.getCommercialData());
    }

    private LoanProduct getEntityOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found: " + id));
    }

    // =====================================================
    // 🔹 GET PRODUCTS BY LOAN TYPE (DEPENDENT PICKLIST)
    // =====================================================
    public List<LoanProductDTO> getProductsByLoanType(String loanType) {

        LoanType type = LoanType.valueOf(loanType); // ✅ STRING → ENUM

        return repository.findByLoanTypeAndIsActiveTrue(type)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // =====================================================
    // 🔹 GET PRODUCT CONFIG (FULL LOGIC DRIVER)
    // =====================================================
    public LoanProduct getProductConfig(
            String productCode,
            String loanType,
            String scheme
    ) {

        return repository
                .findByProductCodeAndLoanTypeAndLoanSchemeAndIsActiveTrue(
                        ProductCode.valueOf(productCode),
                        LoanType.valueOf(loanType),
                        LoanScheme.valueOf(scheme)
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Product Selection"));
    }

    // =====================================================
    // 🔹 REQUIRED DOCUMENTS (🔥 USED NEXT)
    // =====================================================
    public List<String> getApplicantRequiredDocs(LoanProduct product) {

        if (product.getApplicantDocuments() == null) return List.of();

        return product.getApplicantDocuments()
                .stream()
                .map(Enum::name)
                .toList();
    }

    public List<String> getApplicationRequiredDocs(LoanProduct product) {

        if (product.getApplicationDocuments() == null) return List.of();

        return product.getApplicationDocuments()
                .stream()
                .map(Enum::name)
                .toList();
    }

    // =====================================================
    // 🔹 DTO MAPPER
    // =====================================================
    private LoanProductDTO toDTO(LoanProduct product) {

        LoanProductDTO dto = new LoanProductDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());

        // ✅ SAFE ENUM → STRING
        dto.setProductCode(
                product.getProductCode() != null
                        ? product.getProductCode().name()
                        : null
        );

        dto.setLoanScheme(
                product.getLoanScheme() != null
                        ? product.getLoanScheme().name()
                        : null
        );

        dto.setMinAmount(product.getMinLoanAmount());
        dto.setMaxAmount(product.getMaxLoanAmount());

        // ✅ SAFE NULL HANDLING
        dto.setApplicantDocs(
                product.getApplicantDocuments() == null
                        ? List.of()
                        : product.getApplicantDocuments()
                        .stream()
                        .map(Enum::name)
                        .toList()
        );

        dto.setApplicationDocs(
                product.getApplicationDocuments() == null
                        ? List.of()
                        : product.getApplicationDocuments()
                        .stream()
                        .map(Enum::name)
                        .toList()
        );

        return dto;
    }
}