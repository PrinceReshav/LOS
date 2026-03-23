package com.los.loanoriginatingsystem.event;

import com.los.loanoriginatingsystem.event.core.BaseEvent;

public class DocumentProcessedEvent extends BaseEvent {

    private final String documentId;

    public DocumentProcessedEvent(String documentId) {
        super(EventTypes.DOCUMENT_PROCESSED);
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }
}