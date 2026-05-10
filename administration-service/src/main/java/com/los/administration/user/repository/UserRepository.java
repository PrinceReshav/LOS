package com.los.administration.user.repository;

import com.los.administration.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUserId(String userId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);

    @Query("""
    SELECT u FROM User u
    WHERE u.username = :username
       OR u.email = :email
    """)
    Optional<User> findByUsernameOrEmail(
            @Param("username") String username,
            @Param("email") String email
    );


    @Query("""
    SELECT u FROM User u
    JOIN FETCH u.role
    JOIN FETCH u.profile
    WHERE u.userId = :userId
    """)
    Optional<User> findDetailedByUserId(@Param("userId") String userId);



    @EntityGraph(attributePaths = {"role", "profile"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);


    @Query("""
SELECT u FROM User u, RoleClosure rc
WHERE u.role.roleId = rc.descendantRoleId
  AND rc.ancestorRoleId = :roleId
""")
    List<User> findSubordinateUsers(@Param("roleId") String roleId);


    @Query("""
SELECT u FROM User u, RoleClosure rc
WHERE u.role.roleId = rc.ancestorRoleId
  AND rc.descendantRoleId = :roleId
""")
    List<User> findManagers(@Param("roleId") String roleId);

    @Query("""
SELECT u FROM User u
WHERE u.userId IN (
    SELECT uv.targetUserId
    FROM UserVisibility uv
    WHERE uv.viewerUserId = :viewerId
)
""")
    Page<User> findVisibleUsers(
            @Param("viewerId") String viewerId,
            Pageable pageable
    );



    @EntityGraph(attributePaths = {"role", "profile"})
    @Query("""
SELECT u FROM User u
JOIN UserVisibility uv
  ON u.userId = uv.targetUserId
WHERE uv.viewerUserId = :viewerId

  AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))

  AND (:employeeId IS NULL OR u.employeeId = :employeeId)

  AND (:roleName IS NULL OR LOWER(u.role.roleName) = LOWER(:roleName))

  AND (:profileName IS NULL OR LOWER(u.profile.profileName) = LOWER(:profileName))

  AND (:startsWith IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT(:startsWith, '%')))

  AND (:active IS NULL OR u.active = :active)
""")
    Page<User> findVisibleUsersWithFilters(
            @Param("viewerId") String viewerId,
            @Param("username") String username,
            @Param("employeeId") String employeeId,
            @Param("roleName") String roleName,
            @Param("profileName") String profileName,
            @Param("startsWith") String startsWith,
            @Param("active") Boolean active,
            Pageable pageable
    );

    List<User> findByRole_RoleId(String roleId);


}
