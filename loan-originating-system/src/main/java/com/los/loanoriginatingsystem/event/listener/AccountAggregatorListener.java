package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;
import com.los.loanoriginatingsystem.banking.accountaggregator.service.AccountAggregatorService;
import com.los.loanoriginatingsystem.event.AACompletedEvent;
import com.los.loanoriginatingsystem.event.DocumentProcessedEvent;
import com.los.loanoriginatingsystem.event.EventTypes;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountAggregatorListener
        implements TypedEventListener<DocumentProcessedEvent> {

    private final AccountAggregatorService aaService;
    private final EventDispatcher dispatcher;

    @Override
    public String getEventType() {
        return EventTypes.DOCUMENT_PROCESSED;
    }

    @Override
    public void handle(DocumentProcessedEvent event, EventContext context) {

        String docId = event.getDocumentId();

        // ✅ Step 1: Fetch document from context
        String applicationId = context.get("applicationId", String.class);

        // ✅ Step 2: Call AA
        BankStatementResponseDTO response =
                aaService.downloadBankStatement(docId);

        // ✅ Step 3: Fire next event
        dispatcher.dispatchAsync(
                new AACompletedEvent(applicationId),
                new EventContext(Map.of(
                        "applicationId", applicationId,
                        "aaResponse", response
                ))
        );
    }
}