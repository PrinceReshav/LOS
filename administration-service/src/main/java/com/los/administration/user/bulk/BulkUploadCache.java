package com.los.administration.user.bulk;

import com.los.administration.user.dto.UserCreateRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BulkUploadCache {

    private final Map<String, List<UserCreateRequest>> cache = new ConcurrentHashMap<>();

    public void put(String uploadId, List<UserCreateRequest> users) {
        cache.put(uploadId, users);
    }

    public List<UserCreateRequest> get(String uploadId) {
        return cache.get(uploadId);
    }

    public void remove(String uploadId) {
        cache.remove(uploadId);
    }
}