package com.los.administration.auth.util;

import com.los.administration.common.exception.BadRequestException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUserId() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
                auth == null ||
                        !auth.isAuthenticated() ||
                        auth instanceof AnonymousAuthenticationToken
        ) {

            throw new BadRequestException(
                    "USER_NOT_AUTHENTICATED"
            );

        }

        return auth.getName();
    }
}