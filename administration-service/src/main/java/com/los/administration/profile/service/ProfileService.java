package com.los.administration.profile.service;

import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.profile.dto.ProfileRequest;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public Profile createProfile(ProfileRequest request) {

        if (profileRepository.existsByProfileName(request.getProfileName())) {
            throw new IllegalArgumentException("Profile name already exists");
        }

        if (profileRepository.findByProfileId(request.getProfileId()).isPresent()) {
            throw new IllegalArgumentException("Profile id already exists");
        }

        Profile profile = Profile.builder()
                .profileId(request.getProfileId())
                .profileName(request.getProfileName())
                .description(request.getDescription())
                .systemDefined(false)
                .active(true)
                .build();

        return profileRepository.save(profile);
    }

    @Transactional
    public Profile updateProfile(String profileId, ProfileRequest request) {

        Profile profile = getEntity(profileId);

        if (request.getProfileName() != null) profile.setProfileName(request.getProfileName());
        if (request.getDescription() != null) profile.setDescription(request.getDescription());
        if (request.getActive() != null) profile.setActive(request.getActive());

        return profileRepository.save(profile);
    }

    @Transactional
    public Profile setActive(String profileId, boolean active) {

        Profile profile = getEntity(profileId);

        if (Boolean.TRUE.equals(profile.getSystemDefined()) && !active) {
            throw new IllegalStateException("System-defined profiles cannot be deactivated: " + profileId);
        }

        profile.setActive(active);

        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Profile getEntity(String profileId) {

        return profileRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + profileId));
    }

    @Transactional(readOnly = true)
    public Profile getById(String profileId) {
        return getEntity(profileId);
    }

    @Transactional(readOnly = true)
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
}
