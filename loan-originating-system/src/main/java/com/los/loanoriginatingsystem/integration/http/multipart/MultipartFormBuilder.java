package com.los.loanoriginatingsystem.integration.http.multipart;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class MultipartFormBuilder {

    private MultipartFormBuilder() {}

    public static MultiValueMap<String, Object> build(
            Map<String, String> fields,
            Map<String, byte[]> files) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        if (fields != null) {
            fields.forEach(body::add);
        }

        if (files != null) {
            files.forEach((fileName, fileBytes) -> {

                ByteArrayResource resource = new ByteArrayResource(fileBytes) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                };

                body.add("file", resource);
            });
        }

        return body;
    }
}