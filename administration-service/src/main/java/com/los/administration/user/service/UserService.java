package com.los.administration.user.service;

import com.los.administration.command.UserStatusCommandService;
import com.los.administration.common.exception.ResourceNotFoundException;
import com.los.administration.profile.model.Profile;
import com.los.administration.profile.repository.ProfileRepository;
import com.los.administration.role.model.Role;
import com.los.administration.role.repository.RoleRepository;
import com.los.administration.user.bulk.BulkUploadCache;
import com.los.administration.user.bulk.BulkUploadError;
import com.los.administration.user.bulk.BulkUploadMode;
import com.los.administration.user.bulk.BulkUserUploadResult;
import com.los.administration.user.bulk.dto.BulkUploadPreviewResponse;
import com.los.administration.user.bulk.dto.BulkUploadPreviewRow;
import com.los.administration.user.dto.UserCreateRequest;
import com.los.administration.user.dto.UserResponse;
import com.los.administration.user.mapper.UserMapper;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import com.los.administration.user.spec.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.los.administration.user.excel.ExcelParser;

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



    private final UserCreationService userCreationService;

    private final UserStatusCommandService commandService;


    public boolean hasAnyUser() {
        return userRepository.count() > 0;
    }



    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserCreateRequest req) {
        return userCreationService.createUser(req, "SYSTEM_ADMIN");
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
        Role role = roleRepository.findByRoleId(user.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Role not found for user: %s", userId))
                );

        Profile profile = profileRepository.findByProfileId(user.getProfileId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Profile not found for user: %s", userId))
                );

        log.info("USER ACTIVATED | userId={}", userId);

        return toResponse(user, role, profile);
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
        Role role = roleRepository.findByRoleId(user.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Role not found for user: %s", userId))
                );

        Profile profile = profileRepository.findByProfileId(user.getProfileId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Profile not found for user: %s", userId))
                );

        log.info("USER DEACTIVATED | userId={}", userId);

        return toResponse(user, role, profile);
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

        String roleId = null;
        if (roleName != null) {
            roleId = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    String.format("Role not found: %s", roleName)))
                    .getRoleId();
        }

        String profileId = null;
        if (profileName != null) {
            profileId = profileRepository.findByProfileName(profileName)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    String.format("Profile not found: %s", profileName)))
                    .getProfileId();
        }

        Specification<User> spec =
                UserSpecification.usernameLike(username)
                        .and(UserSpecification.employeeIdEquals(employeeId))
                        .and(UserSpecification.roleIdEquals(roleId))
                        .and(UserSpecification.profileIdEquals(profileId))
                        .and(UserSpecification.activeEquals(active))
                        .and(UserSpecification.usernameStartsWith(startsWith));

        return userRepository.findAll(spec, pageable)
                .map(user -> {
                    Role role = roleRepository.findByRoleId(user.getRoleId())
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            String.format("Role not found for userId= %s", user.getUserId())
                                    )
                            );

                    Profile profile = profileRepository.findByProfileId(user.getProfileId())
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            String.format("Profile not found for userId= %s", user.getUserId())
                                    )
                            );
                    return UserMapper.toResponse(user, role, profile);
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
}