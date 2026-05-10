    package com.los.loanoriginatingsystem.document.service;



    import com.los.loanoriginatingsystem.document.dto.BankStatementDocDTO;
    import com.los.loanoriginatingsystem.document.entity.Document;
    import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.List;


    @Service
    @RequiredArgsConstructor
    public class BankStatementDocumentService {

        private final DocumentRepository documentRepository;

        public List<BankStatementDocDTO> getBankStatementDocs(String applicationId) {

            List<Document> docs =
                    documentRepository.findBankStatements(applicationId);

            List<BankStatementDocDTO> result = new ArrayList<>();

            int i = 1;

            for (Document doc : docs) {

                BankStatementDocDTO dto = new BankStatementDocDTO();

                dto.setDocId(doc.getId());

                if (Boolean.TRUE.equals(doc.getBankStatementPrimary())) {
                    dto.setDocNumber("Bank Statement " + i + " (Primary)");
                } else {
                    dto.setDocNumber("Bank Statement " + i);
                }

                dto.setViewFile(doc.getPreviewUrl());

                result.add(dto);
                i++;
            }

            return result;
        }
    }