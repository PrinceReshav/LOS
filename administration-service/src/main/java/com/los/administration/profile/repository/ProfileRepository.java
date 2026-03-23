package com.los.administration.profile.repository;

import com.los.administration.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByProfileId(String profileId);
    Optional<Profile> findByProfileName(String profileName);
    boolean existsByProfileName(String profileName);
}
