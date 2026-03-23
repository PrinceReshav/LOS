package com.los.loanoriginatingsystem.banking.bankstatement.validation;

import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankStatementDuplicateValidator {

    private final DocumentRepository documentRepository;

    public void validateDuplicateBankAccount(
            String applicationId,
            String accountNumber,
            String documentId) {

        boolean exists =
                documentRepository
                        .existsByApplicationIdAndAccountNumberAndIdNot(
                                applicationId,
                                accountNumber,
                                documentId
                        );

        if (exists) {
            throw new IllegalStateException(
                    "Duplicate bank statement for account: " + accountNumber
            );
        }
    }
}