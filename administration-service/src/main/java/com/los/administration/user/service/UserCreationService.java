package com.los.administration.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.los.administration.auth.util.SecurityUtils;
import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.grpc.EmployeeGrpcClient;
import com.los.administration.orgrole.client.OrgRoleClient;
import com.los.administration.orgrole.dto.OrgRoleResponse;
import com.los.administration.license.model.UserLicenseType;
import com.los.administration.outbox.model.OutboxEvent;
import com.los.administration.outbox.model.OutboxStatus;
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
    private final EmployeeGrpcClient employeeGrpcClient;
    private final OrgRoleClient orgRoleClient;


    private static final String USER_PREFIX = "USR_";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserResponse createUser(
            UserCreateRequest req,
            String createdByUserId
    ) {

        // String currentUser =
        //         SecurityUtils.getCurrentUserId();

        String currentUser = createdByUserId;

        Role role =
                roleRepository.findByRoleName(
                        req.getRoleName()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found"
                        )
                );

        Profile profile =
                profileRepository.findByProfileName(
                        req.getProfileName()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Profile not found"
                        )
                );

        // FIX: resolve the ORGANIZATIONAL role (FIELD_OFFICER,
        // RELATIONSHIP_MANAGER, CEO, ...) from los-admin-service's own
        // role catalog up front, and fail fast here with a clean error
        // if it doesn't exist - instead of silently forwarding
        // administration-service's system-access role (role_admin/
        // role_sales) and letting it blow up later as "Role not found"
        // when a branch or reporting manager is assigned.
        OrgRoleResponse orgRole =
                orgRoleClient.getActiveOrgRole(req.getOrgRoleId());

        if (userRepository.existsByUsername(
                req.getUsername()
        )) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(
                req.getEmail()
        )) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        if (userRepository.existsByMobile(
                req.getMobile()
        )) {

            throw new IllegalArgumentException(
                    "Mobile already exists"
            );
        }

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
        user.setOrgRoleId(orgRole.getRoleId());
        user.setOrgRoleName(orgRole.getRoleName());

        user.setLicenseType(
                UserLicenseType.valueOf(
                        req.getLicenseType()
                )
        );

        user.setActive(true);

        user.setCreatedByUserId(currentUser);
        user.setUpdatedByUserId(currentUser);

        User saved =
                userRepository.save(user);

        UserCreatedEvent event =
                new UserCreatedEvent(

                        saved.getUserId(),
                        saved.getEmployeeId(),

                        saved.getUsername(),
                        saved.getEmail(),
                        saved.getMobile(),

                        saved.getFirstName(),
                        saved.getLastName(),

                        role.getRoleId(),
                        role.getRoleName(),

                        // FIX: added so the Kafka/outbox fallback path in
                        // los-admin-service (UserCreatedConsumer ->
                        // EmployeeService.createEmployeeFromUserEvent)
                        // also gets the real org role, instead of falling
                        // back to administration-service's access role.
                        orgRole.getRoleId(),
                        orgRole.getRoleName(),

                        profile.getProfileId(),
                        profile.getProfileName()
                );

        OutboxEvent outbox =
                new OutboxEvent();

        outbox.setAggregateType("USER");
        outbox.setAggregateId(saved.getUserId());
        outbox.setEventType("USER_CREATED");
        outbox.setStatus(
                OutboxStatus.PENDING
        );

        try {

            outbox.setPayload(
                    objectMapper.writeValueAsString(
                            event
                    )
            );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to serialize event",
                    ex
            );
        }

        outboxRepository.save(outbox);
        eventPublisher.publishEvent(event);
        try {

            boolean grpcSuccess =
                    employeeGrpcClient.createEmployee(

                            saved.getEmployeeId(),
                            saved.getUserId(),

                            saved.getFirstName()
                                    + " "
                                    + saved.getLastName(),

                            saved.getEmail(),
                            saved.getMobile(),

                            // FIX: was saved.getRole().getRoleId()/getRoleName()
                            // (administration-service's access role,
                            // e.g. "role_sales") - this is the exact
                            // change that fixes "Role not found" during
                            // branch/reporting-manager assignment.
                            saved.getOrgRoleId(),
                            saved.getOrgRoleName(),

                            saved.getProfile().getProfileId(),
                            saved.getProfile().getProfileName()
                    );

            if (grpcSuccess) {

                outbox.setStatus(
                        OutboxStatus.GRPC_SUCCESS
                );

                outboxRepository.save(outbox);

                log.info(
                        "EMPLOYEE_CREATED_VIA_GRPC userId={}",
                        saved.getUserId()
                );

            } else {

                log.warn(
                        "EMPLOYEE_GRPC_UNAVAILABLE userId={}",
                        saved.getUserId()
                );
            }

        } catch (Exception ex) {

            log.warn(
                    "EMPLOYEE_GRPC_FAILED userId={}",
                    saved.getUserId()
            );
        }

        try {

            incrementalVisibilityService
                    .onUserCreated(saved);

        } catch (Exception ex) {

            log.error(
                    "VISIBILITY_BUILD_FAILED userId={}",
                    saved.getUserId(),
                    ex
            );
        }

        return UserMapper.toResponse(
                saved,
                role,
                profile
        );
    }

    private String generateUserId() {
        return USER_PREFIX + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
    }
}