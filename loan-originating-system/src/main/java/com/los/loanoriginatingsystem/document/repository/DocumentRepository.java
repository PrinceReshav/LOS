package com.los.loanoriginatingsystem.document.repository;

import com.los.loanoriginatingsystem.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository
        extends JpaRepository<Document, String> {

    List<Document> findByTempLoanId(String tempLoanId);

    List<Document> findByLoanApplicantId(String loanApplicantId);

    List<Document> findByLoanApplicationId(String loanApplicationId);

    boolean existsByTempLoanIdAndDocumentType(
            String tempLoanId,
            String documentType
    );

    Optional<Document> findByTempLoanIdAndDocumentType(
            String tempLoanId,
            String documentType
    );

    Optional<Document> findByLoanApplicationIdAndDocumentType(
            String loanApplicationId,
            String documentType
    );
}   