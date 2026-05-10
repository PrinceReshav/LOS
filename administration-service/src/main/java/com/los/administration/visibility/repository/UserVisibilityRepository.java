package com.los.administration.visibility.repository;

import com.los.administration.visibility.model.UserVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVisibilityRepository
        extends JpaRepository<UserVisibility, Long> {

    List<UserVisibility> findByViewerUserId(String viewerUserId);

    boolean existsByViewerUserIdAndTargetUserId(
            String viewerUserId,
            String targetUserId
    );


    void deleteByViewerUserId(String viewerUserId);

    void deleteByTargetUserId(String targetUserId);
}
