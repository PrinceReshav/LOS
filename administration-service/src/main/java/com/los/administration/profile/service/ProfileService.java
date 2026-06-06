package com.los.administration.profile.service;

import com.los.administration.profile.model.Profile;
import com.los.administration.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class    ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public Profile createProfile(Profile profile) {
        if (profileRepository.existsByProfileName(profile.getProfileName())) {
            throw new IllegalArgumentException("Profile name already exists");
        }
        profile.setActive(true);
        return profileRepository.save(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
}
