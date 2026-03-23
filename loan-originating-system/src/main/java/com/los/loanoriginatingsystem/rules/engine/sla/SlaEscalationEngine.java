package com.los.loanoriginatingsystem.rules.engine.sla;

import com.los.loanoriginatingsystem.rules.entity.Deviation;
import com.los.loanoriginatingsystem.rules.entity.SlaConfig;
import com.los.loanoriginatingsystem.rules.repository.DeviationRepository;
import com.los.loanoriginatingsystem.rules.repository.SlaConfigRepository;
import com.los.loanoriginatingsystem.rules.service.DeviationAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlaEscalationEngine {

    private final DeviationRepository deviationRepository;
    private final SlaConfigRepository slaRepository;
    private final DeviationAuditService auditService;

    @Transactional
    public void processEscalations() {

        List<Deviation> pendingDeviations =
                deviationRepository.findByStatus("PENDING");

        if (pendingDeviations.isEmpty()) return;

        List<SlaConfig> configs = slaRepository.findByActiveTrue();

        for (Deviation dev : pendingDeviations) {

            SlaConfig config = findConfig(dev.getCurrentLevel(), configs);

            if (config == null) continue;

            LocalDateTime baseTime =
                    dev.getLastUpdatedAt() != null
                            ? dev.getLastUpdatedAt()
                            : dev.getCreatedAt();

            LocalDateTime deadline =
                    baseTime.plusMinutes(config.getTimeoutMinutes());

            if (LocalDateTime.now().isBefore(deadline)) {
                continue;
            }

            log.warn("SLA breached for deviation {}", dev.getId());

            applyAction(dev, config);
        }
    }

    private SlaConfig findConfig(Integer level, List<SlaConfig> configs) {

        return configs.stream()
                .filter(c -> c.getDeviationLevel().equals(level))
                .findFirst()
                .orElse(null);
    }

    private void applyAction(Deviation dev, SlaConfig config) {

        switch (config.getAction()) {

            case "ESCALATE":
                dev.setCurrentLevel(config.getNextLevel());
                dev.setStatus("L" + config.getNextLevel() + "_PENDING");
                break;

            case "AUTO_APPROVE":
                dev.setFinalStatus("APPROVED");
                dev.setStatus("APPROVED");
                break;

            case "AUTO_REJECT":
                dev.setFinalStatus("REJECTED");
                dev.setStatus("REJECTED");
                break;
        }

        dev.setLastUpdatedAt(LocalDateTime.now());

        deviationRepository.save(dev);

        auditService.log(
                dev.getId(),
                "SLA_" + config.getAction(),
                dev.getCurrentLevel(), // ✅ Integer
                "SYSTEM",
                "SLA triggered"
        );
    }
}