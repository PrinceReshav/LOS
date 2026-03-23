package com.los.administration.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditLogService {

    public void log(AuditLogEntry entry) {

        log.info(
                "AUDIT | userId={} | role={} | method={} | endpoint={} | action={} | status={} | success={}",
                entry.getUserId(),
                entry.getRole(),
                entry.getHttpMethod(),
                entry.getEndpoint(),
                entry.getAction(),
                entry.getHttpStatus(),
                entry.isSuccess()
        );
    }
}