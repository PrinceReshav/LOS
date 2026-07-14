package com.los.loanoriginatingsystem.document.service;

public interface DocumentMigrationService {

    void migrateDocuments(
            String tempId,
            String applicantId,
            String loanApplicationId
    );
}