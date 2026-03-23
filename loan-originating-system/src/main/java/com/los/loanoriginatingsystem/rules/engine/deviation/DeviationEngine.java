package com.los.loanoriginatingsystem.rules.engine.deviation;

import com.los.events.NotificationEvent;
import com.los.loanoriginatingsystem.outbox.service.OutboxService;
import com.los.loanoriginatingsystem.rules.entity.Deviation;
import com.los.loanoriginatingsystem.rules.model.BusinessRuleResult;
import com.los.loanoriginatingsystem.rules.repository.DeviationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeviationEngine {

    private final DeviationRepository deviationRepository;
    private final OutboxService outboxService;
    /**
     * 🔥 CREATE / REOPEN / SKIP
     */
    public void createDeviations(
            String applicationId,
            String targetId,
            String targetType,
            List<BusinessRuleResult> results
    ) {

        List<Deviation> existing =
                deviationRepository.findByApplicationIdAndTargetId(applicationId, targetId);

        Map<String, Deviation> existingMap =
                existing.stream().collect(Collectors.toMap(
                        Deviation::getRuleId,
                        d -> d
                ));

        List<Deviation> toInsert = new ArrayList<>();
        List<Deviation> toUpdate = new ArrayList<>();

        for (BusinessRuleResult result : results) {

            String ruleId = result.getRuleId();
            Integer level = result.getDeviationLevel(); // ✅ INTEGER

            // 🔥 CASE 1: already exists
            if (existingMap.containsKey(ruleId)) {

                Deviation dev = existingMap.get(ruleId);

                // ✅ approved → ignore
                if ("APPROVED".equalsIgnoreCase(dev.getStatus())) {
                    continue;
                }

                // ✅ already pending → skip
                if (dev.getStatus() != null && dev.getStatus().contains("PENDING")) {
                    continue;
                }

                // 🔥 reopen
                dev.setCurrentLevel(level);
                dev.setStatus("L" + level + "_PENDING");
                dev.setUpdatedAt(LocalDateTime.now());

                toUpdate.add(dev);
                continue;
            }

            // 🔥 CASE 2: new deviation
            Deviation dev = new Deviation();

            dev.setId(UUID.randomUUID().toString());
            dev.setRuleId(ruleId);
            dev.setRuleName(result.getRuleName());

            dev.setApplicationId(applicationId);
            dev.setTargetId(targetId);
            dev.setTargetType(targetType);

            dev.setDeviationLevel(level);   // ✅ FIXED
            dev.setCurrentLevel(level);     // ✅ IMPORTANT

            dev.setStatus("L" + level + "_PENDING");
            dev.setCreatedAt(LocalDateTime.now());

            toInsert.add(dev);
        }

        if (!toUpdate.isEmpty()) {
            deviationRepository.saveAll(toUpdate);
        }
        if (!toInsert.isEmpty()) {
            deviationRepository.saveAll(toInsert);

            // 🔥 SEND NOTIFICATION
            outboxService.saveEvent(

                    "DEVIATION",
                    applicationId,
                    "NOTIFICATION",
                    new NotificationEvent(
                            UUID.randomUUID().toString(),
                            "EMAIL",
                            "DEVIATION_CREATED",
                            List.of("ops@company.com"),
                            Map.of("applicationId", applicationId)
                    )
            );
        }
    }

    /**
     * 🔥 AUTO RESOLVE
     */
    public void resolveDeviations(
            String applicationId,
            String targetId,
            List<String> failedRuleIds
    ) {

        List<Deviation> existing =
                deviationRepository.findByApplicationIdAndTargetId(applicationId, targetId);

        List<Deviation> toUpdate = new ArrayList<>();

        for (Deviation dev : existing) {

            if (!failedRuleIds.contains(dev.getRuleId())
                    && dev.getStatus() != null
                    && dev.getStatus().contains("PENDING")) {

                dev.setStatus("RESOLVED");
                dev.setUpdatedAt(LocalDateTime.now());

                toUpdate.add(dev);
            }
        }

        if (!toUpdate.isEmpty()) {
            deviationRepository.saveAll(toUpdate);
        }
    }

    public List<Deviation> getPending(String applicationId) {
        return deviationRepository.findByApplicationIdAndStatus(applicationId, "PENDING");
    }

    public void updateStatus(String deviationId,
                             String status,
                             String user,
                             String comment) {

        Deviation deviation = deviationRepository.findById(deviationId)
                .orElseThrow();

        deviation.setStatus(status);
        deviation.setApprovedBy(user);
        deviation.setApprovedAt(LocalDateTime.now());
        deviation.setComment(comment);
        deviation.setUpdatedAt(LocalDateTime.now());

        deviationRepository.save(deviation);
    }
}