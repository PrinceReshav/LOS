package com.los.administration.security.aspect;

import com.los.administration.auth.util.SecurityUtils;
import com.los.administration.security.annotation.*;
import com.los.administration.security.service.SecurityPermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final SecurityPermissionService securityPermissionService;

    @Before("@annotation(permission)")
    public void checkPermission(RequiresPermission permission) {

        String userId = SecurityUtils.getCurrentUserId();

        securityPermissionService.checkPermission(
                userId,
                permission.object(),
                permission.action()
        );
    }
}
