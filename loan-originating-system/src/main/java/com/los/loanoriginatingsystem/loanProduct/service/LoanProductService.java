package com.los.loanoriginatingsystem.loanProduct.service;

import com.los.loanoriginatingsystem.loanProduct.dto.LoanProductDTO;
import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanScheme;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanType;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.ProductCode;
import com.los.loanoriginatingsystem.loanProduct.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanProductService {

    private final LoanProductRepository repository;

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