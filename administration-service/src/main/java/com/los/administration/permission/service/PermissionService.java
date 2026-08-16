package com.los.administration.permission.service;

import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.permission.model.Permission;
import com.los.administration.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository repository;

    @Transactional
    public Permission create(Permission permission) {

        if (repository.existsByPermissionCode(permission.getPermissionCode())) {
            throw new IllegalArgumentException("Permission code already exists");
        }

        permission.setActive(true);

        return repository.save(permission);
    }

    @Transactional
    public Permission update(String id, Permission changes) {

        Permission existing = getById(id);

        if (changes.getPermissionName() != null) existing.setPermissionName(changes.getPermissionName());
        if (changes.getModuleName() != null) existing.setModuleName(changes.getModuleName());
        if (changes.getDescription() != null) existing.setDescription(changes.getDescription());
        if (changes.getActive() != null) existing.setActive(changes.getActive());

        return repository.save(existing);
    }

    @Transactional(readOnly = true)
    public Permission getById(String id) {

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Permission> getAll() {
        return repository.findAll();
    }
}
