package com.los.administration.security.aspect;

import com.los.administration.auth.util.SecurityUtils;
import com.los.administration.security.annotation.*;
import com.los.administration.security.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    @Before("@annotation(permission)")
    public void checkPermission(RequiresPermission permission) {

        String userId = SecurityUtils.getCurrentUserId();

        permissionService.checkPermission(
                userId,
                permission.object(),
                permission.action()
        );
    }
}




/*
 *   private final PermissionService permissionService;

    @Before("@annotation(canRead)")
    public void checkRead(CanRead canRead) {

        String userId = SecurityUtils.getCurrentUserId();

        permissionService.checkPermission(
                userId,
                canRead.value(),
                "READ"
        );
    }

    @Before("@annotation(canCreate)")
    public void checkCreate(CanCreate canCreate) {

        String userId = SecurityUtils.getCurrentUserId();

        permissionService.checkPermission(
                userId,
                canCreate.value(),
                "CREATE"
        );
    }

    @Before("@annotation(canUpdate)")
    public void checkUpdate(CanUpdate canUpdate) {

        String userId = SecurityUtils.getCurrentUserId();

        permissionService.checkPermission(
                userId,
                canUpdate.value(),
                "UPDATE"
        );
    }

    @Before("@annotation(canDelete)")
    public void checkDelete(CanDelete canDelete) {

        String userId = SecurityUtils.getCurrentUserId();

        permissionService.checkPermission(
                userId,
                canDelete.value(),
                "DELETE"
        );
    }
*/