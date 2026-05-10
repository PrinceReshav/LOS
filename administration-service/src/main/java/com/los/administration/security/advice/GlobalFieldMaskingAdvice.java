package com.los.administration.security.advice;

import com.los.administration.auth.util.SecurityUtils;
import com.los.administration.security.model.FieldPermission;
import com.los.administration.security.service.FieldSecurityService;
import com.los.administration.security.util.FieldFilterUtil;
import com.los.administration.user.model.User;
import com.los.administration.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.lang.Nullable;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalFieldMaskingAdvice implements ResponseBodyAdvice<Object> {

    private final FieldSecurityService fieldSecurityService;
    private final FieldFilterUtil fieldFilterUtil;
    private final UserRepository userRepository;

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true; // apply to all responses
    }

    @Override
    public Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response
    ){

        if (body == null) return null;

        String userId = SecurityUtils.getCurrentUserId();

        User currentUser = userRepository.findByUserId(userId).orElse(null);
        if (currentUser == null) return body;

        String profileId = currentUser.getProfile().getProfileId();

        // 🔥 HANDLE ApiResponse wrapper
        if (body instanceof com.los.administration.common.dto.ApiResponse<?> apiResponse) {

            Object data = apiResponse.getData();

            Object filtered = applyFilter(data, profileId);

            ((com.los.administration.common.dto.ApiResponse<Object>) apiResponse)
                    .setData(filtered);

            return apiResponse;
        }

        return applyFilter(body, profileId);
    }

    private Object applyFilter(Object data, String profileId) {

        if (data == null) return null;

        // 🔁 PAGE SUPPORT
        if (data instanceof org.springframework.data.domain.Page<?> page) {

            List<?> filtered =
                    page.getContent().stream()
                            .map(obj -> filterSingle(obj, profileId))
                            .toList();

            return new org.springframework.data.domain.PageImpl<>(
                    filtered,
                    page.getPageable(),
                    page.getTotalElements()
            );
        }

        // 🔁 LIST SUPPORT
        if (data instanceof List<?> list) {

            return list.stream()
                    .map(obj -> filterSingle(obj, profileId))
                    .toList();
        }

        // 🔁 SINGLE OBJECT
        return filterSingle(data, profileId);
    }

    private Object filterSingle(Object dto, String profileId) {

        if (dto == null) return null;

        if (dto instanceof String ||
                dto instanceof Number ||
                dto instanceof Boolean ||
                dto.getClass().isEnum() ||
                dto.getClass().getPackageName().startsWith("java.")) {
            return dto;
        }

        String objectName = resolveObjectName(dto);

        Map<String, FieldPermission> permissions =
                fieldSecurityService.getPermissions(profileId, objectName);

        return fieldFilterUtil.filter(dto, permissions);
    }

    private String resolveObjectName(Object dto) {

        // SIMPLE RULE: DTO name → object
        // UserResponse → USER
        return dto.getClass()
                .getSimpleName()
                .replace("Response", "")
                .toUpperCase();
    }
}