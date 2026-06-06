package com.los.administration.security.util;

import com.los.administration.security.model.SecurityFieldPermission;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

@Component
public class FieldWriteFilterUtil {

    public <T> void validateWrite(T dto, Map<String, SecurityFieldPermission> permissions) {

        try {
            for (Field field : dto.getClass().getDeclaredFields()) {

                field.setAccessible(true);

                Object value = field.get(dto);

                if (value == null) continue; // not being updated

                String fieldName = field.getName();

                SecurityFieldPermission perm = permissions.get(fieldName);

                if (perm == null) continue;

                if (!Boolean.TRUE.equals(perm.getCanWrite())) {
                    throw new RuntimeException(
                            "WRITE_ACCESS_DENIED for field: " + fieldName
                    );
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Write FLS validation failed", e);
        }
    }
}
