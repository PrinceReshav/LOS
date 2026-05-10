package com.los.administration.role.service;

import com.los.administration.role.model.Role;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.visibility.service.RoleClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleClosureService roleClosureService;

    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new IllegalArgumentException("Role name already exists");
        }
        role.setActive(true);



        validateNoCycle(role);
        Role saved = roleRepository.save(role);

        roleClosureService.addRole(saved); // ✅ IMPORTANT

        return saved;
    }

    private void validateNoCycle(Role role) {

        Role parent = role.getParentRole();

        while (parent != null) {
            if (parent.getRoleId().equals(role.getRoleId())) { // object reference check
                throw new IllegalStateException("Role hierarchy cycle detected");
            }
            parent = parent.getParentRole();
        }
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
