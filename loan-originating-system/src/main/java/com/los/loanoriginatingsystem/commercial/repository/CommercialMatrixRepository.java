package com.los.loanoriginatingsystem.commercial.repository;

import com.los.loanoriginatingsystem.commercial.entity.CommercialMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommercialMatrixRepository
        extends JpaRepository<CommercialMatrix, String>, JpaSpecificationExecutor<CommercialMatrix> {
}
