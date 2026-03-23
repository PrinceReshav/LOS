package com.los.administration.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.los.events.UserCreatedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCreationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private static final String USER_PREFIX = "USR_";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponse createUser(
            UserCreateRequest req,
            String createdByUserId
    ) {

        Role role = roleRepository.findByRoleName(req.getRoleName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Role not found: %s", req.getRoleName())
                        )
                );

        Profile profile = profileRepository.findByProfileName(req.getProfileName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Profile not found: %s", req.getProfileName())
                        )
                );

        User user = new User();
        user.setUserId(generateUserId());
        if(userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setUsername(req.getUsername());
        if(userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setEmail(req.getEmail());
        if(userRepository.existsByMobile(req.getMobile())) {
            throw new IllegalArgumentException("Mobile already exists");
        }
        user.setMobile(req.getMobile());
        user.setAlias(req.getAlias());
        user.setFirstName(req.getFirstName());
        user.setMiddleName(req.getMiddleName());
        user.setLastName(req.getLastName());
        user.setEmployeeId(req.getEmployeeId());
        user.setRoleId(role.getRoleId());
        user.setProfileId(profile.getProfileId());
        user.setActive(true);
        user.setCreatedByUserId(createdByUserId);
        user.setUpdatedByUserId(createdByUserId);

        User saved = userRepository.save(user);

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

        // Internal Spring event (email/password)
        eventPublisher.publishEvent(event);

        // Save event to OUTBOX
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

        return UserMapper.toResponse(saved, role, profile);
    }

    private String generateUserId() {
        return USER_PREFIX + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
    }
}