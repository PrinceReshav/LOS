package com.los.loanoriginatingsystem.rules.scheduler;

import com.los.loanoriginatingsystem.rules.engine.sla.SlaEscalationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaScheduler {

    private final SlaEscalationEngine slaEscalationEngine;

    /**
     * 🔥 Runs every 1 minute
     */
    @Scheduled(fixedDelay = 60000)
    public void runSlaJob() {

        log.info("Running SLA Escalation Job");

        try {
            slaEscalationEngine.processEscalations();
        } catch (Exception e) {
            log.error("SLA job failed", e);
        }
    }
}