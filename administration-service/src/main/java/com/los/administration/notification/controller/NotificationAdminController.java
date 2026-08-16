package com.los.administration.notification.controller;

import com.los.administration.common.dto.ApiResponse;
import com.los.administration.notification.entity.NotificationDLQ;
import com.los.administration.notification.entity.NotificationLog;
import com.los.administration.notification.entity.NotificationTemplate;
import com.los.administration.notification.repository.NotificationDLQRepository;
import com.los.administration.notification.repository.NotificationLogRepository;
import com.los.administration.notification.repository.NotificationTemplateRepository;
import com.los.administration.security.annotation.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only visibility for HR/Admin into what the notification pipeline
 * (templates, delivery log, dead-letter queue) is actually doing - the
 * pipeline itself (/internal/notifications/send, retry scheduler, DLQ
 * consumer) already exists; this just exposes it to the Setup UI so it
 * isn't a black box.
 */
@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class NotificationAdminController {

    private final NotificationLogRepository logRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationDLQRepository dlqRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "NOTIFICATION", action = "READ")
    @GetMapping("/logs")
    public ApiResponse<Page<NotificationLog>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return ApiResponse.success(
                logRepository.findAll(pageable),
                "Notification logs fetched successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "NOTIFICATION", action = "READ")
    @GetMapping("/templates")
    public ApiResponse<List<NotificationTemplate>> getTemplates() {
        return ApiResponse.success(
                templateRepository.findAll(),
                "Notification templates fetched successfully"
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequiresPermission(object = "NOTIFICATION", action = "READ")
    @GetMapping("/dead-letters")
    public ApiResponse<List<NotificationDLQ>> getDeadLetters() {
        return ApiResponse.success(
                dlqRepository.findAll(),
                "Dead-lettered notifications fetched successfully"
        );
    }
}
