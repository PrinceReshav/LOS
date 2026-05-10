package com.los.administration.user.bulk;

import com.los.administration.user.dto.UserCreateRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BulkUploadCache {

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public void put(String uploadId, List<UserCreateRequest> users) {
        cache.put(uploadId, new CacheEntry(users));
    }


    public List<UserCreateRequest> get(String uploadId) {

        CacheEntry entry = cache.get(uploadId);

        if (entry == null) {
            throw new IllegalArgumentException("Invalid uploadId");
        }

        // TTL = 10 minutes
        if (System.currentTimeMillis() - entry.timestamp > 10 * 60 * 1000) {
            cache.remove(uploadId);
            throw new IllegalArgumentException("Upload expired");
        }

        return entry.data;
    }

    public void remove(String uploadId) {
        cache.remove(uploadId);
    }
    private static class CacheEntry {
        List<UserCreateRequest> data;
        long timestamp;

        CacheEntry(List<UserCreateRequest> data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Scheduled(fixedRate = 300000)
    public void cleanup() {
        long now = System.currentTimeMillis();

        cache.entrySet().removeIf(e ->
                now - e.getValue().timestamp > 10 * 60 * 1000
        );
    }

}