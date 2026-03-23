package com.los.loanoriginatingsystem.rules.service;

import com.los.loanoriginatingsystem.rules.entity.Deviation;
import com.los.loanoriginatingsystem.rules.model.DeviationResponseDTO;
import com.los.loanoriginatingsystem.rules.repository.DeviationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeviationService {

    private final DeviationRepository deviationRepository;

    /**
     * 🔥 GET ALL PENDING DEVIATIONS GROUPED BY TARGET
     */
    public List<DeviationResponseDTO> getPendingDeviations(String applicationId) {

        List<Deviation> deviations =
                deviationRepository.findByApplicationIdAndStatus(applicationId, "PENDING");

        Map<String, DeviationResponseDTO> grouped = new HashMap<>();

        for (Deviation d : deviations) {

            grouped.putIfAbsent(d.getTargetId(), new DeviationResponseDTO());

            DeviationResponseDTO dto = grouped.get(d.getTargetId());

            dto.setTargetId(d.getTargetId());
            dto.setTargetName(d.getTargetName());
            dto.setObjectName("UNKNOWN"); // can enhance later

            if (dto.getDeviations() == null) {
                dto.setDeviations(new ArrayList<>());
            }

            DeviationResponseDTO.DeviationItem item =
                    new DeviationResponseDTO.DeviationItem();

            item.setDeviationId(d.getId());
            item.setRuleName(d.getRuleName());
            item.setLevel("L" + d.getDeviationLevel());
            item.setStatus(d.getStatus());

            dto.getDeviations().add(item);
        }

        return new ArrayList<>(grouped.values());
    }

    /**
     * 🔥 APPROVE / REJECT
     */
    public void updateStatus(String deviationId, String status, String user, String comment) {

        Deviation deviation = deviationRepository.findById(deviationId)
                .orElseThrow();

        deviation.setStatus(status);
        deviation.setApprovedBy(user);
        deviation.setApprovedAt(java.time.LocalDateTime.now());
        deviation.setComment(comment);

        deviationRepository.save(deviation);
    }

    /**
     * 🔥 COUNT PENDING
     */
    public long countPending(String applicationId) {

        return deviationRepository
                .findByApplicationIdAndStatus(applicationId, "PENDING")
                .size();
    }
}