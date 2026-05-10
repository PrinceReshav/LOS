package com.los.loanoriginatingsystem.loanProduct.repository;

import com.los.loanoriginatingsystem.loanProduct.entity.LoanProduct;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanScheme;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.LoanType;
import com.los.loanoriginatingsystem.loanProduct.entity.enums.ProductCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanProductRepository
        extends JpaRepository<LoanProduct, String> {

    List<LoanProduct> findByLoanTypeAndIsActiveTrue(LoanType loanType);

    List<LoanProduct> findByProductCodeAndLoanTypeAndLoanSchemeAndIsActiveTrue(
            ProductCode productCode,
            LoanType loanType,
            LoanScheme loanScheme
    );
}