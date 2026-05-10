package com.los.loanoriginatingsystem.audit.service;

import com.los.loanoriginatingsystem.audit.dto.AuditResponseDTO;
import com.los.loanoriginatingsystem.audit.entity.ActionAudit;
import com.los.loanoriginatingsystem.audit.repository.ActionAuditRepository;

import com.los.loanoriginatingsystem.audit.spec.AuditSpecification;
import com.los.loanoriginatingsystem.audit.util.MaskingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditQueryService {

    private final ActionAuditRepository repo;

    public List<AuditResponseDTO> getAudit(
            String entityType,
            String entityId,
            String action,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {

        return repo
                .findAll(
                        AuditSpecification.filter(entityType, entityId, action, from, to),
                        PageRequest.of(page, size)
                )
                .map(this::toDTO)
                .getContent();
    }

    private AuditResponseDTO toDTO(ActionAudit audit) {

        AuditResponseDTO dto = new AuditResponseDTO();

        dto.setAction(audit.getAction());
        dto.setPerformedBy(audit.getPerformedBy());
        dto.setRole(audit.getRole());
        dto.setOldStatus(audit.getOldStatus());
        dto.setNewStatus(audit.getNewStatus());
        dto.setCreatedAt(audit.getCreatedAt());
        // 🔐 MASK sensitive
        dto.setRemarks(MaskingUtil.mask(audit.getRemarks()));

        dto.setCreatedAt(audit.getCreatedAt());

        return dto;
    }


    // =============================
    // 📄 CSV EXPORT
    // =============================
    public String exportCsv(List<AuditResponseDTO> list) {

        StringBuilder sb = new StringBuilder();

        sb.append("Action,User,Role,OldStatus,NewStatus,Remarks,CreatedAt\n");

        for (AuditResponseDTO dto : list) {

            sb.append(dto.getAction()).append(",")
                    .append(dto.getPerformedBy()).append(",")
                    .append(dto.getRole()).append(",")
                    .append(dto.getOldStatus()).append(",")
                    .append(dto.getNewStatus()).append(",")
                    .append(dto.getRemarks()).append(",")
                    .append(dto.getCreatedAt())
                    .append("\n");
        }

        return sb.toString();
    }

}