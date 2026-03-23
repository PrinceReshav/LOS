package com.los.loanoriginatingsystem.document.repository;

import com.los.loanoriginatingsystem.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository
        extends JpaRepository<Document, String> {

    @Query("""
           SELECT d FROM Document d
           WHERE d.applicationId = :applicationId
           AND d.documentType = 'Account Aggregator'
           """)
    Optional<Document> findAccountAggregatorDocument(String applicationId);


    @Query("""
       SELECT d FROM Document d
       WHERE d.applicationId = :applicationId
       AND (d.documentType = 'Bank Statement'
            OR d.documentType = 'Account Aggregator')
       """)
    List<Document> findBankStatements(String applicationId);

    List<Document> findByApplicationId(String applicationId);

    boolean existsByApplicationIdAndAccountNumber(
            String applicationId,
            String accountNumber
    );

    @Modifying
    @Query("""
        UPDATE Document d
        SET d.aaProcessing = true
        WHERE d.id = :documentId
        AND (d.aaProcessing IS NULL OR d.aaProcessing = false)
       """)
    int lockForProcessing(String documentId);

    boolean existsByApplicationIdAndAccountNumberAndIdNot(
            String applicationId,
            String accountNumber,
            String id
    );

}