package com.los.administration.security.service;

import com.los.administration.security.model.SecurityFieldPermission;
import com.los.administration.security.repository.FieldPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FieldSecurityService {

    private final FieldPermissionRepository repository;

    public Map<String, SecurityFieldPermission> getPermissions(
            String profileId,
            String object
    ) {

        return repository.findByProfileIdAndObjectName(profileId, object)
                .stream()
                .collect(Collectors.toMap(
                        SecurityFieldPermission::getFieldName,
                        fp -> fp
                ));
    }
}
