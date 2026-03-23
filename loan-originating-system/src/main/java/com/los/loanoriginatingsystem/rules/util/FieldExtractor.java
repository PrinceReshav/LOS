package com.los.loanoriginatingsystem.rules.util;


import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class FieldExtractor {

    public Object extract(Object source, String fieldPath) {

        try {
            String[] fields = fieldPath.split("\\.");
            Object current = source;

            for (String fieldName : fields) {

                Field field = current.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                current = field.get(current);

                if (current == null) return null;
            }

            return current;

        } catch (Exception e) {
            return null;
        }
    }
}