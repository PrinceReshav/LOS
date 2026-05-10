package com.los.administration.visibility.service;

import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.dto.ManualShareRequest;
import com.los.administration.visibility.model.AccessType;
import com.los.administration.visibility.model.ManualShare;
import com.los.administration.visibility.repository.ManualShareRepository;
import com.los.administration.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManualShareService {

    private final ManualShareRepository repository;
    private final UserVisibilityService userVisibilityService;
    private final UserRepository userRepository;
    private final IncrementalVisibilityService incrementalVisibilityService;

    @Transactional
    public void share(ManualShareRequest req) {

        ManualShare share = ManualShare.builder()
                .ownerUserId(req.getOwnerUserId())
                .sharedWithUserId(req.getSharedWithUserId())
                .accessType(AccessType.MANUAL)
                .build();

        repository.save(share);

        // ✅ rebuild only for owner
        User owner = userRepository.findByUserId(req.getOwnerUserId())
                .orElseThrow();

        incrementalVisibilityService.onUserCreated(owner);
    }
}