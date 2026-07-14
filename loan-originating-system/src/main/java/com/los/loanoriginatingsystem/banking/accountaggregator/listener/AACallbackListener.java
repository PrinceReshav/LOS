/*
package com.los.loanoriginatingsystem.banking.accountaggregator.listener;

import com.los.loanoriginatingsystem.banking.accountaggregator.event.AACallbackEvent;
import com.los.loanoriginatingsystem.banking.accountaggregator.service.AccountAggregatorWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AACallbackListener {

    private final AccountAggregatorWorkflowService workflowService;

    @Async
    @EventListener
    public void handle(AACallbackEvent event) {

        log.info("Processing AA workflow for document {}", event.getDocumentId());

        workflowService.processBankStatementAsync(event.getDocumentId());
    }
}
*/