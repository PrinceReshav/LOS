package com.los.administration.visibility.repository;


import com.los.administration.visibility.model.RoleClosure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleClosureRepository
        extends JpaRepository<RoleClosure, Long> {

    List<RoleClosure> findByAncestorRoleId(String roleId);

    List<RoleClosure> findByDescendantRoleId(String roleId);

    boolean existsByAncestorRoleIdAndDescendantRoleId(
            String ancestor,
            String descendant
    );
}