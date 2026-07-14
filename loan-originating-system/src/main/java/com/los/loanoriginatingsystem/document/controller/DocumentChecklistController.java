package com.los.loanoriginatingsystem.document.controller;

import com.los.loanoriginatingsystem.document.dto.DocumentChecklistResponse;
import com.los.loanoriginatingsystem.document.service.DocumentChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentChecklistController {

    private final DocumentChecklistService service;

    @GetMapping("/checklist/{tempId}")
    public DocumentChecklistResponse getChecklist(
            @PathVariable String tempId
    ) {
        return service.getChecklist(tempId);
    }

    // The frontend's DocumentChecklistPanel / documentApi.getRequiredDocuments
    // call this path — kept as an alias of /checklist/{tempId}.
    @GetMapping("/required/{tempId}")
    public DocumentChecklistResponse getRequiredDocuments(
            @PathVariable String tempId
    ) {
        return service.getChecklist(tempId);
    }
}