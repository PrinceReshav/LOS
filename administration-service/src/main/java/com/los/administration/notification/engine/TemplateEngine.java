package com.los.administration.notification.engine;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateEngine {

    public String process(String template, Map<String, Object> data) {

        String result = template;

        if (data == null) return result;

        for (Map.Entry<String, Object> entry : data.entrySet()) {

            String key = "{{" + entry.getKey() + "}}";
            String value = String.valueOf(entry.getValue());

            result = result.replace(key, value);
        }

        return result;
    }
}