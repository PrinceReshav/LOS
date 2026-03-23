package com.los.administration.user.repository;

import com.los.administration.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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



}
