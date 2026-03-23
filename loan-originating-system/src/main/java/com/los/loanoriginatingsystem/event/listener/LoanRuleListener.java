package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.event.EventTypes;
import com.los.loanoriginatingsystem.event.LoanUpdatedEvent;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.loan.entity.LoanApplication;
import com.los.loanoriginatingsystem.loan.repository.LoanApplicationRepository;
import com.los.loanoriginatingsystem.rules.engine.BusinessRuleExecutor;
import com.los.loanoriginatingsystem.rules.engine.deviation.DeviationEngine;
import com.los.loanoriginatingsystem.rules.model.BusinessRuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanRuleListener implements TypedEventListener<LoanUpdatedEvent> {

    private static final String OBJECT_NAME = "LoanApplication";

    private final BusinessRuleExecutor ruleExecutor;
    private final LoanApplicationRepository loanRepository;
    private final DeviationEngine deviationEngine;

    @Override
    public String getEventType() {
        return EventTypes.LOAN_UPDATED;
    }

    @Override
    public void handle(LoanUpdatedEvent event, EventContext context) {

        if (event == null || event.getApplicationId() == null) {
            log.warn("LoanRuleListener received invalid event");
            return;
        }

        String applicationId = event.getApplicationId();

        log.info("Executing Loan Rules for applicationId={}", applicationId);

        LoanApplication loan =
                loanRepository.findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Loan not found: " + applicationId));

        // 🔥 STEP 1: Execute Rules
        List<BusinessRuleResult> results =
                ruleExecutor.execute(OBJECT_NAME, loan);

        if (results.isEmpty()) {
            log.info("No rule failures for applicationId={}", applicationId);
            return;
        }

        // 🔥 STEP 2: Create Deviations
        deviationEngine.createDeviations(
                applicationId,
                loan.getId(),
                OBJECT_NAME,
                results
        );

        log.info("Created {} deviations for applicationId={}", results.size(), applicationId);
    }
}