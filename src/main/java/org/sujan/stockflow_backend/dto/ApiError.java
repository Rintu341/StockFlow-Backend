package org.sujan.stockflow_backend.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        int status,
        String code,                       // stable machine-readable id
        String message,                    // safe to show to the user
        Map<String, String> fieldErrors,   // field name -> message
        Instant timestamp
) {
    public static ApiError of(HttpStatus status, String code, String message) {
        return new ApiError(status.value(), code, message, Map.of(), Instant.now());
    }
}