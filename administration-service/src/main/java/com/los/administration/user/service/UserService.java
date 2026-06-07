package com.los.administration.user.service;

import com.los.administration.auth.util.SecurityUtils;
import com.los.administration.command.UserStatusCommandService;
import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.repository.ProfileRepository;
import com.los.administration.role.model.Role;
import com.los.administration.role.model.RoleType;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.security.model.SecurityFieldPermission;
import com.los.administration.security.service.FieldSecurityService;
import com.los.administration.security.util.FieldFilterUtil;
import com.los.administration.security.util.FieldWriteFilterUtil;
import com.los.administration.user.bulk.BulkUploadCache;
import com.los.administration.user.bulk.BulkUploadError;
import com.los.administration.user.bulk.BulkUploadMode;
import com.los.administration.user.bulk.BulkUserUploadResult;
import com.los.administration.user.bulk.dto.BulkUploadPreviewResponse;
import com.los.administration.user.bulk.dto.BulkUploadPreviewRow;
import com.los.administration.user.dto.UserCreateRequest;
import com.los.administration.user.dto.UserProfileResponse;
import com.los.administration.user.dto.UserResponse;
import com.los.administration.user.dto.UserUpdateRequest;
import com.los.administration.user.mapper.UserMapper;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.visibility.service.IncrementalVisibilityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.los.administration.user.excel.ExcelParser;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final ExcelParser<UserCreateRequest> excelParser;
    private final BulkUploadCache  bulkUploadCache;
    private final IncrementalVisibilityService incrementalVisibilityService;
    private final FieldSecurityService fieldSecurityService;
    private final FieldFilterUtil fieldFilterUtil;


    private final UserCreationService userCreationService;
    private final FieldWriteFilterUtil fieldWriteFilterUtil;
    private final UserStatusCommandService commandService;


    public boolean hasAnyUser() {
        return userRepository.count() > 0;
    }



    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserCreateRequest req) {
        String currentUser = SecurityUtils.getCurrentUserId();
        return userCreationService.createUser(req, currentUser);

    }


    @Transactional
    public UserResponse updateUser(String userId, UserUpdateRequest req) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 🔥 GET PERMISSIONS
        String currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        String profileId = currentUser.getProfile().getProfileId();
        // 🔒 HARDENING: prevent privilege escalation
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            if (req.getRoleName() != null || req.getProfileName() != null) {
                throw new AccessDeniedException("Cannot modify role or profile");
            }
        }

        Map<String, SecurityFieldPermission> permissions =
                fieldSecurityService.getPermissions(profileId, "USER");

        // 🔥 WRITE VALIDATION
        fieldWriteFilterUtil.validateWrite(req, permissions);

        // 🔥 APPLY UPDATES (only allowed fields passed validation)
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getMobile() != null) user.setMobile(req.getMobile());
        if (req.getAlias() != null) user.setAlias(req.getAlias());
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getMiddleName() != null) user.setMiddleName(req.getMiddleName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());

        // 🔥 SENSITIVE FIELDS (extra protection)
        if (req.getRoleName() != null) {
            Role role = roleRepository.findByRoleName(req.getRoleName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            user.setRole(role);
        }

        if (req.getProfileName() != null) {
            Profile profile = profileRepository.findByProfileName(req.getProfileName())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
            user.setProfile(profile);
        }

        if (req.getActive() != null) {
            user.setActive(req.getActive());
        }

        user.setUpdatedByUserId(currentUserId);

        userRepository.save(user);

        return UserMapper.toResponse(user, user.getRole(), user.getProfile());
    }


    @Transactional
    public UserResponse activateUser(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User not found: %s", userId))
                );

        user.setActive(true);
        user.setUpdatedByUserId("SYSTEM_ADMIN");

        try {
            commandService.activateEmployee(userId);
        } catch (Exception ex) {
            throw new RuntimeException("Employee service unavailable", ex);
        }

        Role role = getRole(user);
        Profile profile = getProfile(user);
        log.info("USER ACTIVATED | userId={}", userId);
        incrementalVisibilityService.onUserActivated(user);


        UserResponse response = toResponse(user, role, profile);

        Map<String, SecurityFieldPermission> permissions =
                getCurrentUserPermissions();
        return fieldFilterUtil.filter(response, permissions);
    }


    @Transactional
    public UserResponse deactivateUser(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User not found: %s", userId))
                );

        user.setActive(false);
        user.setUpdatedByUserId("SYSTEM_ADMIN");

        try {
            commandService.deactivateEmployee(userId);
        } catch (Exception ex) {
            throw new RuntimeException("Employee service unavailable", ex);
        }


        Role role = getRole(user);
        Profile profile = getProfile(user);

        log.info("USER DEACTIVATED | userId={}", userId);
        incrementalVisibilityService.onUserDeactivated(user);


        UserResponse response = toResponse(user, role, profile);

        String currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        String profileId = currentUser.getProfile().getProfileId();
        fieldSecurityService.getPermissions(profileId, "USER");

        Map<String, SecurityFieldPermission> permissions =
                getCurrentUserPermissions();
        return fieldFilterUtil.filter(response, permissions);
    }


    private UserResponse toResponse(User user, Role role, Profile profile) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .alias(user.getAlias())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .employeeId(user.getEmployeeId())
                .roleName(role.getRoleName())
                .profileName(profile.getProfileName())
                .licenseType(user.getLicenseType())
                .active(user.getActive())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(
            String username,
            String employeeId,
            String roleName,
            String profileName,
            Boolean active,
            String startsWith,
            Pageable pageable
    ) {

        String currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findByUserId(currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Current user not found"));

        Map<String, SecurityFieldPermission> permissions = getCurrentUserPermissions();

        username = normalize(username);
        roleName = normalize(roleName);
        profileName = normalize(profileName);
        startsWith = normalize(startsWith);
        employeeId = normalize(employeeId);


        log.debug(
                "USER_SEARCH | viewer={} username={} role={} profile={} employeeId={} startsWith={} active={}",
                currentUserId,
                username,
                roleName,
                profileName,
                employeeId,
                startsWith,
                active
        );

        if (currentUser.getRole().getRoleType() == RoleType.ROOT
                || "ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName())) {

            return userRepository.findAll(pageable)
                    .map(user -> {

                        UserResponse dto =
                                UserMapper.toResponse(
                                        user,
                                        user.getRole(),
                                        user.getProfile()
                                );

                        return fieldFilterUtil.filter(dto, permissions);
                    });
        }

        return userRepository.findVisibleUsersWithFilters(
                currentUserId,
                username,
                employeeId,
                roleName,
                profileName,
                startsWith,
                active,
                pageable
        ).map(user -> {

            UserResponse dto =
                    UserMapper.toResponse(user, user.getRole(), user.getProfile());

            return fieldFilterUtil.filter(dto, permissions);
        });
    }


    public BulkUserUploadResult bulkUploadUsers(MultipartFile file) {

        List<UserResponse> successUsers = new ArrayList<>();
        List<BulkUploadError> errors = new ArrayList<>();

        List<UserCreateRequest> rows = excelParser.parse(file);

        if (rows.size() > 200) {
            throw new IllegalArgumentException("Maximum 200 users allowed per upload");
        }

        int excelRowNumber = 2; // header is row 1

        for (UserCreateRequest req : rows) {
            try {
                UserResponse user =
                        userCreationService.createUser(req, "SYSTEM_ADMIN");
                successUsers.add(user);

            } catch (DataIntegrityViolationException ex) {

                errors.add(
                        BulkUploadError.builder()
                                .rowNumber(excelRowNumber)
                                .field("DATABASE_CONSTRAINT")
                                .message("Duplicate or constraint violation")
                                .rawValue(ex.getMostSpecificCause().getMessage())
                                .build()
                );

            } catch (Exception ex) {

                errors.add(
                        BulkUploadError.builder()
                                .rowNumber(excelRowNumber)
                                .field("GENERAL")
                                .message(ex.getMessage())
                                .rawValue(req.getEmail())
                                .build()
                );
            }

            excelRowNumber++;
        }

        return BulkUserUploadResult.builder()
                .totalRecords(rows.size())
                .successCount(successUsers.size())
                .failureCount(errors.size())
                .successUsers(successUsers)
                .errors(errors)
                .preview(false)
                .mode(BulkUploadMode.PARTIAL)
                .build();
    }

    public BulkUploadPreviewResponse validateUsers(MultipartFile file) {

        List<UserCreateRequest> parsedRows = excelParser.parse(file);

        List<BulkUploadPreviewRow> previewRows = new ArrayList<>();
        List<UserCreateRequest> validUsers = new ArrayList<>();
        Set<String> mobileSet = new HashSet<>();
        Set<String> usernameSet = new HashSet<>();
        Set<String> emailSet = new HashSet<>();

        int rowNum = 2; // assuming header is row 1

        for (UserCreateRequest req : parsedRows) {

            List<String> rowErrors = new ArrayList<>();
            boolean isValid = true;

            if (userRepository.existsByUsername(req.getUsername())) {
                rowErrors.add("Username already exists");
                isValid = false;
            }

            if (userRepository.existsByEmail(req.getEmail())) {
                rowErrors.add("Email already exists");
                isValid = false;
            }

            if (userRepository.existsByMobile(req.getMobile())) {
                rowErrors.add("Mobile already exists");
                isValid = false;
            }

            // --- FILE DUPLICATE CHECKS ---
            if (!usernameSet.add(req.getUsername())) {
                rowErrors.add("Duplicate username in file");
                isValid = false;
            }

            if (!emailSet.add(req.getEmail())) {
                rowErrors.add("Duplicate email in file");
                isValid = false;
            }

            if (!mobileSet.add(req.getMobile())) {
                rowErrors.add("Duplicate mobile in file");
                isValid = false;
            }

            if (isValid) {
                validUsers.add(req);
            }

            previewRows.add(
                    BulkUploadPreviewRow.builder()
                            .rowNumber(rowNum)
                            .data(req)
                            .valid(isValid)
                            .errors(rowErrors)
                            .build()
            );

            rowNum++;
        }

        String uploadId = UUID.randomUUID().toString();
        bulkUploadCache.put(uploadId, validUsers);

        return BulkUploadPreviewResponse.builder()
                .uploadId(uploadId)
                .totalRecords(parsedRows.size())
                .validRecords(validUsers.size())
                .invalidRecords(parsedRows.size() - validUsers.size())
                .rows(previewRows)
                .build();
    }

    @Transactional
    public void updateUserRole(String userId, String newRoleName) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role oldRole = user.getRole();

        Role newRole = roleRepository.findByRoleName(newRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(newRole);

        userRepository.save(user);

        // ✅ EXACT LINE YOU ASKED
        incrementalVisibilityService.onRoleChanged(user, oldRole);
    }

    @Transactional
    public BulkUserUploadResult commitUpload(String uploadId) {

        List<UserCreateRequest> users = bulkUploadCache.get(uploadId);

        if (users == null) {
            throw new IllegalArgumentException("Invalid uploadId");
        }

        List<UserResponse> success = new ArrayList<>();
        List<BulkUploadError> errors = new ArrayList<>();

        int rowNumber = 2;

        for (UserCreateRequest req : users) {

            try {
                UserResponse created =
                        userCreationService.createUser(req, "SYSTEM_ADMIN");

                success.add(created);

            } catch (Exception ex) {

                errors.add(
                        BulkUploadError.builder()
                                .rowNumber(rowNumber)
                                .field("COMMIT")
                                .message(ex.getMessage())
                                .rawValue(req.getEmail())
                                .build()
                );
            }

            rowNumber++;
        }

        bulkUploadCache.remove(uploadId);

        return BulkUserUploadResult.builder()
                .totalRecords(users.size())
                .successCount(success.size())
                .failureCount(errors.size())
                .successUsers(success)
                .errors(errors)
                .preview(false)
                .mode(BulkUploadMode.PARTIAL)
                .build();
    }

    private Map<String, SecurityFieldPermission> getCurrentUserPermissions() {

        String currentUserId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        String profileId = currentUser.getProfile().getProfileId();

        return fieldSecurityService.getPermissions(profileId, "USER");
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private Role getRole(User user) {

        return roleRepository.findByRoleId(
                user.getRole().getRoleId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format(
                                "Role not found for user: %s",
                                user.getUserId()
                        )
                )
        );
    }

    private Profile getProfile(User user) {

        return profileRepository.findByProfileId(
                user.getProfile().getProfileId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format(
                                "Profile not found for user: %s",
                                user.getUserId()
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {

        String userId =
                SecurityUtils.getCurrentUserId();

        User user =
                userRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())

                .firstName(user.getFirstName())
                .lastName(user.getLastName())

                .email(user.getEmail())
                .mobile(user.getMobile())

                .employeeId(user.getEmployeeId())

                .role(
                        user.getRole() != null
                                ? user.getRole().getRoleName()
                                : null
                )

                .profile(
                        user.getProfile() != null
                                ? user.getProfile().getProfileName()
                                : null
                )

                .active(user.getActive())

                .build();
    }



}