package com.los.administration.common.exception;

import com.los.administration.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- Validation Errors ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("VALIDATION FAILED: {}", errorMessage);

        return ApiResponse.error(errorMessage);
    }

    // ---------- Business Errors (400) ----------
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadRequest(BadRequestException ex) {

        log.warn("BAD REQUEST: {}", ex.getMessage());

        return ApiResponse.error(ex.getMessage());
    }

    // ---------- Not Found (404) ----------
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFound(ResourceNotFoundException ex) {

        log.warn("NOT FOUND: {}", ex.getMessage());

        return ApiResponse.error(ex.getMessage());
    }

    // ---------- Fallback (500) ----------
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleUnknown(Throwable ex) {

        log.error("UNEXPECTED ERROR", ex);

        return ApiResponse.error(
                "Internal server error. Please contact support."
        );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("VALIDATION ERROR: {}", ex.getMessage());

        return ApiResponse.error(ex.getMessage());
    }

    // ---------- Business Rule Conflicts (409) ----------
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<?> handleIllegalState(IllegalStateException ex) {

        log.warn("BUSINESS RULE CONFLICT: {}", ex.getMessage());

        return ApiResponse.error(ex.getMessage());
    }

    // ---------- Access Denied (403) ----------
    // Covers both @PreAuthorize/@RequiresPermission AOP failures and
    // AccessDeniedException thrown directly from service code (e.g.
    // SecurityPermissionService, visibility checks).
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {

        log.warn("ACCESS DENIED: {}", ex.getMessage());

        return ApiResponse.error(
                ex.getMessage() != null ? ex.getMessage() : "You do not have permission to perform this action"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {

        String message = "Database constraint violation";

        if (ex.getMostSpecificCause() != null) {

            String cause = ex.getMostSpecificCause().getMessage();

            if (cause.contains("USERS(MOBILE")) {
                message = "Mobile number already exists";
            }
            else if (cause.contains("USERS(EMAIL")) {
                message = "Email already exists";
            }
            else if (cause.contains("USERS(USERNAME")) {
                message = "Username already exists";
            }
        }

        ApiResponse<?> response =
                ApiResponse.error(message);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }
}