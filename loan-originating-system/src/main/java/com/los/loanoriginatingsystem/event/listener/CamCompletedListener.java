package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.event.CamCompletedEvent;
import com.los.loanoriginatingsystem.event.EventTypes;
import com.los.loanoriginatingsystem.event.LoanUpdatedEvent;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CamCompletedListener implements TypedEventListener<CamCompletedEvent> {

    private final EventDispatcher dispatcher;

    @Override
    public String getEventType() {
        return EventTypes.CAM_COMPLETED;
    }

    @Override
    public void handle(CamCompletedEvent event, EventContext context) {

        String applicationId = event.getApplicationId();

        dispatcher.dispatchAsync(
                new LoanUpdatedEvent(applicationId),
                new EventContext(Map.of(
                        "applicationId", applicationId
                ))
        );
    }
}