package com.los.administration.user.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.user.bulk.BulkUserUploadResult;
import com.los.administration.user.bulk.dto.BulkUploadPreviewResponse;
import com.los.administration.user.bulk.store.BulkUploadErrorStore;
import com.los.administration.user.dto.UserCreateRequest;
import com.los.administration.user.dto.UserResponse;
import com.los.administration.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BulkUploadErrorStore bulkUploadErrorStore;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request) {

        UserResponse response = userService.createUser(request);
        return ApiResponse.success(response, "User created successfully");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/activate")
    public ApiResponse<UserResponse> activate(@PathVariable String userId) {
        UserResponse response = userService.activateUser(userId);
        return ApiResponse.success(response, "User activated successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/deactivate")
    public ApiResponse<UserResponse> deactivate(@PathVariable String userId) {
        UserResponse response = userService.deactivateUser(userId);
        return ApiResponse.success(response, "User deactivated successfully");
    }


    @PostMapping("/bulk-upload/validate")
    public ApiResponse<BulkUploadPreviewResponse> validate(
            @RequestParam("file") MultipartFile file
    ) {
        return ApiResponse.success(
                userService.validateUsers(file),
                "Validation complete"
        );
    }

    @PostMapping("/bulk-upload/commit")
    public ApiResponse<BulkUserUploadResult> commit(
            @RequestParam String uploadId
    ) {
        return ApiResponse.success(
                userService.commitUpload(uploadId),
                "Users created successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-upload")
    public ApiResponse<BulkUserUploadResult> bulkUpload(
            @RequestParam("file") MultipartFile file
    ) {

        BulkUserUploadResult result =
                userService.bulkUploadUsers(file);

        return ApiResponse.success(result, "Bulk upload processed");
    }

    @GetMapping("/bulk-upload/errors/{fileId}")
    public ResponseEntity<byte[]> downloadErrors(@PathVariable String fileId) {

        ByteArrayInputStream stream = bulkUploadErrorStore.get(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=bulk-upload-errors.xlsx")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(stream.readAllBytes());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String profileName,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String startsWith,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "username")
        );

        Page<UserResponse> result =
                userService.getUsers(
                        username,
                        employeeId,
                        roleName,
                        profileName,
                        active,
                        startsWith,
                        pageable
                );

        return ApiResponse.success(result, "Users fetched successfully");
    }

}


/*
 * @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String profileName,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<UserResponse> result =
                userService.getUsers(username, employeeId, roleName, profileName, active, pageable);

        return ApiResponse.success(result, "Users fetched successfully");
    }*/