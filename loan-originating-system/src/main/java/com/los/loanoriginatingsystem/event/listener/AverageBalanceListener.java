package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.event.CamCompletedEvent;
import com.los.loanoriginatingsystem.event.LoanUpdatedEvent;
import com.los.loanoriginatingsystem.event.EventTypes;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import com.los.loanoriginatingsystem.banking.camanalysis.service.AverageBankBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AverageBalanceListener implements TypedEventListener<CamCompletedEvent> {

    private final AverageBankBalanceService abbService;
    private final EventDispatcher dispatcher;

    @Override
    public String getEventType() {
        return EventTypes.CAM_COMPLETED;
    }

    @Override
    public void handle(CamCompletedEvent event, EventContext context) {

        String applicationId = event.getApplicationId();

        abbService.updateOrSave(applicationId, "data", null, "Bank Details");

        dispatcher.dispatchAsync(
                new LoanUpdatedEvent(applicationId),
                new EventContext(Map.of("applicationId", applicationId))
        );
    }
}