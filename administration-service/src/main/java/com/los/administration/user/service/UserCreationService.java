package com.los.administration.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.administration.auth.util.SecurityUtils;
import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.outbox.model.OutboxEvent;
import com.los.administration.outbox.repository.OutboxEventRepository;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.repository.ProfileRepository;
import com.los.administration.role.model.Role;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.user.dto.UserCreateRequest;
import com.los.administration.user.dto.UserResponse;
import com.los.administration.user.mapper.UserMapper;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.service.IncrementalVisibilityService;
import com.los.administration.visibility.service.UserVisibilityService;
import com.los.events.UserCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final UserVisibilityService userVisibilityService;
    private final IncrementalVisibilityService incrementalVisibilityService;

    private static final String USER_PREFIX = "USR_";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponse createUser(UserCreateRequest req, String createdByUserId) {

        String currentUser = SecurityUtils.getCurrentUserId();

        Role role = roleRepository.findByRoleName(req.getRoleName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found: " + req.getRoleName()));

        Profile profile = profileRepository.findByProfileName(req.getProfileName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found: " + req.getProfileName()));

        User user = new User();
        user.setUserId(generateUserId());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setMobile(req.getMobile());
        user.setAlias(req.getAlias());
        user.setFirstName(req.getFirstName());
        user.setMiddleName(req.getMiddleName());
        user.setLastName(req.getLastName());
        user.setEmployeeId(req.getEmployeeId());
        user.setRole(role);
        user.setProfile(profile);
        user.setActive(true);
        user.setCreatedByUserId(currentUser);
        user.setUpdatedByUserId(currentUser);

        User saved;
        try {
            saved = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Duplicate username/email/mobile");
        }

        UserCreatedEvent event = new UserCreatedEvent(
                saved.getUserId(),
                saved.getEmployeeId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getMobile(),
                saved.getFirstName(),
                saved.getLastName(),
                role.getRoleId(),
                profile.getProfileId()
        );

        eventPublisher.publishEvent(event);

        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateType("USER");
        outbox.setAggregateId(saved.getUserId());
        outbox.setEventType("USER_CREATED");

        try {
            outbox.setPayload(objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }

        outboxRepository.save(outbox);


        try {
            incrementalVisibilityService.onUserCreated(saved);
        } catch (Exception ex) {
            log.error(
                    "VISIBILITY_BUILD_FAILED | userId={} | error={}",
                    saved.getUserId(),
                    ex.getMessage(),
                    ex
            );
        }

        return UserMapper.toResponse(saved, role, profile);
    }

    private String generateUserId() {
        return USER_PREFIX + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
    }
}