
package com.fincore.gateway.dto;

import java.time.Instant;
import java.util.Map;

public record ApiResponse<T>(
        boolean success,
        String message,
        String path,
        T data,
        Map<String, String> errors,
        Instant timestamp
) {

    public static <T> ApiResponse<T> error(
            String message,
            String path,
            Map<String, String> errors
    ) {
        return new ApiResponse<>(
                false,
                message,
                path,
                null,
                errors,
                Instant.now()
        );
    }
}
