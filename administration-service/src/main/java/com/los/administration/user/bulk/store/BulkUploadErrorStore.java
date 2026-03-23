package com.los.administration.user.bulk.store;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BulkUploadErrorStore {

    private final Map<String, ByteArrayInputStream> store = new ConcurrentHashMap<>();

    public String save(ByteArrayInputStream stream) {
        String id = UUID.randomUUID().toString();
        store.put(id, stream);
        return id;
    }

    public ByteArrayInputStream get(String id) {
        ByteArrayInputStream stream = store.get(id);
        if (stream == null) {
            throw new IllegalArgumentException("Invalid or expired error file id");
        }
        return stream;
    }

    public void remove(String id) {
        store.remove(id);
    }
}