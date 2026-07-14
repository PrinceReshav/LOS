package com.los.loanoriginatingsystem.document.service.impl;

import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.document.service.DocumentMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentMigrationServiceImpl
        implements DocumentMigrationService {

    private final DocumentRepository repository;

    @Override
    public void migrateDocuments(
            String tempId,
            String applicantId,
            String loanApplicationId
    ) {

        List<Document> docs =
                repository.findByTempLoanId(tempId);

        for (Document doc : docs) {

            doc.setLoanApplicationId(
                    loanApplicationId
            );

            if (
                    "Aadhaar Front".equals(doc.getDocumentType())
                            || "Aadhaar Back".equals(doc.getDocumentType())
                            || "PAN Card".equals(doc.getDocumentType())
                            || "Driving License".equals(doc.getDocumentType())
                            || "Voter Id".equals(doc.getDocumentType())
                            || "Customer Photo".equals(doc.getDocumentType())
            ) {

                doc.setLoanApplicantId(
                        applicantId
                );
            }

            doc.setTempLoanId(null);
        }

        repository.saveAll(docs);
    }
}