package com.los.administration.visibility.controller;

import com.los.administration.visibility.dto.RecordShareRequest;
import com.los.administration.visibility.model.RecordShare;
import com.los.administration.visibility.repository.RecordShareRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin CRUD + activation for ad hoc record grants (the exception path - most access should come from role hierarchy or branch match). */
@RestController
@RequestMapping("/admin/record-shares")
@RequiredArgsConstructor
public class RecordShareController {

    private final RecordShareRepository repository;

    @PostMapping
    public RecordShare create(@Valid @RequestBody RecordShareRequest request) {
        if ((request.getSharedWithUserId() == null || request.getSharedWithUserId().isBlank())
                && (request.getSharedWithRoleId() == null || request.getSharedWithRoleId().isBlank())) {
            throw new IllegalArgumentException("Either sharedWithUserId or sharedWithRoleId must be set");
        }

        RecordShare share = RecordShare.builder()
                .recordType(request.getRecordType())
                .recordId(request.getRecordId())
                .sharedWithUserId(request.getSharedWithUserId())
                .sharedWithRoleId(request.getSharedWithRoleId())
                .accessLevel(request.getAccessLevel())
                .reason(request.getReason())
                .active(true)
                .build();

        return repository.save(share);
    }

    @GetMapping
    public List<RecordShare> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{recordType}/{recordId}")
    public List<RecordShare> getForRecord(@PathVariable String recordType, @PathVariable String recordId) {
        return repository.findByRecordTypeAndRecordIdAndActiveTrue(recordType, recordId);
    }

    @PatchMapping("/{id}/activate")
    public RecordShare activate(@PathVariable Long id) {
        RecordShare share = repository.findById(id).orElseThrow();
        share.setActive(true);
        return repository.save(share);
    }

    @PatchMapping("/{id}/deactivate")
    public RecordShare deactivate(@PathVariable Long id) {
        RecordShare share = repository.findById(id).orElseThrow();
        share.setActive(false);
        return repository.save(share);
    }
}
