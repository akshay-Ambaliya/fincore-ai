
package com.fincore.organization.dto.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        String path,
        Instant timestamp,
        T data
) {

    public static <T> ApiResponse<T> success(
            int status,
            String message,
            String path,
            T data
    ) {
        return new ApiResponse<>(
                true,
                status,
                message,
                path,
                Instant.now(),
                data
        );
    }
}
