package com.los.loanoriginatingsystem.saga;

import com.los.loanoriginatingsystem.event.DocumentProcessedEvent;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoanSagaOrchestrator {

    private final EventDispatcher dispatcher;

    public void start(String applicationId) {

        dispatcher.dispatchAsync(
                new DocumentProcessedEvent(applicationId),
                new EventContext(Map.of("applicationId", applicationId))
        );
    }
}