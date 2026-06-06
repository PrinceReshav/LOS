package com.los.administration.permission.service;

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
    public Permission create(Permission permission){

        if(repository.existsByPermissionCode(
                permission.getPermissionCode()
        )){
            throw new RuntimeException(
                    "SecurityPermission already exists"
            );
        }

        permission.setActive(true);

        return repository.save(permission);
    }

    public List<Permission> getAll(){
        return repository.findAll();
    }
}