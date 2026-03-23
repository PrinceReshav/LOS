package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.document.entity.Document;
import com.los.loanoriginatingsystem.event.DocumentProcessedEvent;
import com.los.loanoriginatingsystem.event.EventTypes;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.rules.engine.BusinessRuleExecutor;
import com.los.loanoriginatingsystem.rules.engine.deviation.DeviationEngine;
import com.los.loanoriginatingsystem.rules.model.BusinessRuleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RuleEngineListener implements TypedEventListener<DocumentProcessedEvent> {

    private final BusinessRuleExecutor ruleExecutor;
    private final DeviationEngine deviationEngine;

    @Override
    public String getEventType() {
        return EventTypes.DOCUMENT_PROCESSED;
    }

    @Override
    public void handle(DocumentProcessedEvent event, EventContext context) {

        Document doc = context.get("document", Document.class);
        String applicationId = context.get("applicationId", String.class);

        // 🔥 STEP 1: Execute Rules
        List<BusinessRuleResult> results =
                ruleExecutor.execute("Document", doc);

        // 🔥 STEP 2: Extract failed rule IDs
        List<String> failedRuleIds = results.stream()
                .map(BusinessRuleResult::getRuleId)
                .toList();

        // 🔥 STEP 3: Create / Reopen deviations
        deviationEngine.createDeviations(
                applicationId,
                doc.getId(),
                doc.getDocumentType(),
                results
        );

        // 🔥 STEP 4: Resolve old deviations
        deviationEngine.resolveDeviations(
                applicationId,
                doc.getId(),
                failedRuleIds
        );
    }
}