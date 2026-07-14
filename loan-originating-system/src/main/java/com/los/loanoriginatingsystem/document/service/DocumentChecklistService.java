package com.los.loanoriginatingsystem.document.service;

import com.los.loanoriginatingsystem.document.dto.DocumentChecklistResponse;

public interface DocumentChecklistService {

    DocumentChecklistResponse getChecklist(
            String tempId
    );
}