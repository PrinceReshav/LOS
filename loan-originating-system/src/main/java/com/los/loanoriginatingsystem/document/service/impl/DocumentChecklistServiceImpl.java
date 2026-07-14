package com.los.loanoriginatingsystem.document.service.impl;

import com.los.loanoriginatingsystem.document.dto.DocumentChecklistResponse;
import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.document.entity.DocumentChecklist;
import com.los.loanoriginatingsystem.document.repository.DocumentChecklistRepository;
import com.los.loanoriginatingsystem.document.repository.DocumentRepository;
import com.los.loanoriginatingsystem.document.service.DocumentChecklistService;
import com.los.loanoriginatingsystem.document.util.ApplicantDocumentTypes;
import com.los.loanoriginatingsystem.temp.entity.TempLoanApplication;
import com.los.loanoriginatingsystem.temp.repository.TempLoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentChecklistServiceImpl
        implements DocumentChecklistService {

    private final TempLoanApplicationRepository tempRepo;

    private final DocumentChecklistRepository checklistRepo;

    private final DocumentRepository documentRepo;

    @Override
    public DocumentChecklistResponse getChecklist(
            String tempId
    ) {

        TempLoanApplication temp =
                tempRepo.findById(tempId)
                        .orElseThrow(
                                () -> new RuntimeException("Temp not found")
                        );

        if (temp.getLoanDetails() == null) {

            DocumentChecklistResponse response =
                    new DocumentChecklistResponse();

            response.setApplicantDocuments(List.of());
            response.setApplicationDocuments(List.of());
            response.setAlreadyUploaded(List.of());

            return response;
        }

        String loanProductCode =
                temp.getLoanDetails()
                        .getLoanProductCode();

        List<DocumentChecklist> checklist =
                checklistRepo.findByLoanProductCode(
                        loanProductCode
                );

        List<String> requiredDocs =
                checklist.stream()
                        .map(DocumentChecklist::getDocumentType)
                        .toList();

        List<String> uploadedDocs =
                documentRepo.findByTempLoanId(tempId)
                        .stream()
                        .map(Document::getDocumentType)
                        .toList();

        List<String> applicantDocs =
                requiredDocs.stream()
                        .filter(ApplicantDocumentTypes.TYPES::contains)
                        .toList();

        List<String> applicationDocs =
                requiredDocs.stream()
                        .filter(doc ->
                                !ApplicantDocumentTypes.TYPES.contains(doc)
                        )
                        .toList();

        DocumentChecklistResponse response =
                new DocumentChecklistResponse();

        response.setRequiredDocuments(requiredDocs);
        response.setUploadedDocuments(uploadedDocs);

        response.setApplicantDocuments(applicantDocs);
        response.setApplicationDocuments(applicationDocs);
        response.setAlreadyUploaded(uploadedDocs);

        return response;
    }
}