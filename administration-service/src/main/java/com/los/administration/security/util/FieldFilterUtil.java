package com.los.administration.security.util;

import com.los.administration.security.model.FieldPermission;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

@Component
public class FieldFilterUtil {

    public <T> T filter(T dto, Map<String, FieldPermission> permissions) {

        try {

            for (Field field : dto.getClass().getDeclaredFields()) {

                field.setAccessible(true);

                String fieldName = field.getName();

                FieldPermission perm = permissions.get(fieldName);

                if (perm == null) continue;

                // ❌ NO READ → NULL
                if (!Boolean.TRUE.equals(perm.getCanRead())) {
                    field.set(dto, null);
                    continue;
                }

                // 🔒 MASK
                if (Boolean.TRUE.equals(perm.getMasked())) {

                    Object value = field.get(dto);

                    if (value != null) {
                        field.set(dto, "****");
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("FLS filtering failed", e);
        }

        return dto;
    }
}
