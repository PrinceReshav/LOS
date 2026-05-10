package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.visibility.model.RoleClosure;
import com.los.administration.visibility.repository.RoleClosureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleClosureService {

    private final RoleClosureRepository repository;


    @Transactional
    public void addRole(Role role) {

        // 1. SELF
        save(role.getRoleId(), role.getRoleId(), 0);

        Role parent = role.getParentRole();

        if (parent != null) {

            // get ALL ancestors of parent
            List<RoleClosure> parentClosures =
                    repository.findByDescendantRoleId(parent.getRoleId());

            for (RoleClosure pc : parentClosures) {

                save(
                        pc.getAncestorRoleId(),
                        role.getRoleId(),
                        pc.getDepth() + 1
                );
            }
        }
    }

    private void save(String ancestor, String descendant, int depth) {
        try {
            repository.save(
                    RoleClosure.builder()
                            .ancestorRoleId(ancestor)
                            .descendantRoleId(descendant)
                            .depth(depth)
                            .build()
            );
        } catch (DataIntegrityViolationException ignored) {
            // duplicate row → ignore
        }
    }
}