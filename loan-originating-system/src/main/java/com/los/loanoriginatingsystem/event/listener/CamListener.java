package com.los.loanoriginatingsystem.event.listener;

import com.los.loanoriginatingsystem.banking.accountaggregator.dto.BankStatementResponseDTO;
import com.los.loanoriginatingsystem.banking.camanalysis.dto.BankAccountCAMData;
import com.los.loanoriginatingsystem.banking.camanalysis.service.CamAnalysisService;
import com.los.loanoriginatingsystem.event.AACompletedEvent;
import com.los.loanoriginatingsystem.event.CamCompletedEvent;
import com.los.loanoriginatingsystem.event.EventTypes;
import com.los.loanoriginatingsystem.event.context.EventContext;
import com.los.loanoriginatingsystem.event.dispatcher.EventDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CamListener implements TypedEventListener<AACompletedEvent> {

    private final CamAnalysisService camService;
    private final EventDispatcher dispatcher;

    @Override
    public String getEventType() {
        return EventTypes.AA_COMPLETED;
    }

    @Override
    public void handle(AACompletedEvent event, EventContext context) {

        if (event == null || event.getApplicationId() == null) {
            log.warn("Invalid AACompletedEvent received");
            return;
        }

        String applicationId = event.getApplicationId();

        log.info("CAM processing started for applicationId={}", applicationId);

        // 🔥 SAFELY GET RESPONSE
        BankStatementResponseDTO response =
                context.get("aaResponse", BankStatementResponseDTO.class);

        if (response == null) {
            log.error("AA response missing in context for applicationId={}", applicationId);
            return;
        }

        // 🔥 STEP 1: Run CAM
        List<BankAccountCAMData> camData =
                camService.processBankStatement(response);

        log.info("CAM completed for applicationId={}", applicationId);

        // 🔥 STEP 2: Emit CAM_COMPLETED
        dispatcher.dispatchAsync(
                new CamCompletedEvent(applicationId),
                new EventContext(Map.of(
                        "applicationId", applicationId,
                        "camData", camData
                ))
        );
    }
}