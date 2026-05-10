package com.los.administration.security.repository;

import com.los.administration.security.model.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfilePermissionRepository extends JpaRepository<ProfilePermission, Long> {

    boolean existsByProfileIdAndPermissionIdAndAllowedTrue(
            String profileId,
            Long permissionId
    );

    @Query("""
    SELECT COUNT(pp) > 0
    FROM ProfilePermission pp
    JOIN Permission p ON pp.permissionId = p.id
    WHERE pp.profileId = :profileId
      AND p.objectName = :object
      AND p.action = :action
      AND pp.allowed = true
""")
    boolean hasPermission(String profileId, String object, String action);

}
