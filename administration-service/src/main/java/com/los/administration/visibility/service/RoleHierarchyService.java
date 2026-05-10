package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleHierarchyService {

    private final RoleRepository roleRepository;

    public Set<Role> getAllSubordinates(Role role) {

        Role root = roleRepository.findWithChildren(role.getId());

        Set<Role> result = new HashSet<>();
        collect(root, result);
        return result;
    }

    public boolean isSameRole(Role r1, Role r2) {
        return r1 != null && r2 != null && r1.getId().equals(r2.getId());
    }

    private void collect(Role role, Set<Role> result) {
        for (Role child : role.getChildren()) {
            result.add(child);
            collect(child, result);
        }
    }

    public boolean isManager(Role manager, Role subordinate) {
        if (manager == null || subordinate == null) return false;

        Role current = subordinate.getParentRole();

        while (current != null) {
            if (current.getId().equals(manager.getId())) {
                return true;
            }
            current = current.getParentRole();
        }

        return false;
    }
}