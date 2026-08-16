package com.los.administration.visibility.service;

import com.los.administration.role.model.Role;
import com.los.administration.visibility.model.RoleClosure;
import com.los.administration.visibility.repository.RoleClosureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /**
     * FIX: RoleService.updateRole previously changed a role's parent
     * without ever touching role_closure, so every consumer of the
     * closure table (UserRepository.findSubordinateUsers/findManagers,
     * and therefore IncrementalVisibilityService) kept using the stale
     * ancestor chain from before the move - forever, until the app was
     * restarted or the row was manually fixed.
     *
     * This recomputes the ancestor chain for {@code role} and cascades
     * the same recomputation down its entire subtree, since every
     * descendant's ancestor chain also runs through the moved role.
     */
    @Transactional
    public void rebuildForRole(Role role) {

        // Capture the role's current descendants BEFORE we start
        // deleting anything, since deleting closure rows is how we
        // "forget" the old ancestor chain.
        List<String> descendantIds = new ArrayList<>();
        repository.findByAncestorRoleId(role.getRoleId())
                .forEach(rc -> {
                    if (!rc.getDescendantRoleId().equals(role.getRoleId())) {
                        descendantIds.add(rc.getDescendantRoleId());
                    }
                });

        // Wipe role's own stale ancestor chain (including its self row)
        // and rebuild it against the new parent.
        repository.deleteByDescendantRoleId(role.getRoleId());
        addRole(role);

        // Cascade: every descendant's ancestor chain also passes through
        // role, so it needs to be recomputed too. We rebuild top-down
        // using the live parent/children association rather than the
        // (now partially stale) closure table.
        for (Role child : role.getChildren()) {
            rebuildForRole(child);
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