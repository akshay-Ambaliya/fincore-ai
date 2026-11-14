    package com.fincore.identity.dto.response;

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

        public static <T> ApiResponse<T> success(
                String message, String path, T data) {

            return new ApiResponse<>(
                    true, message, path, data, null, Instant.now()
            );
        }

        public static <T> ApiResponse<T> error(
                String message, String path, Map<String, String> errors) {

            return new ApiResponse<>(
                    false, message, path, null, errors, Instant.now()
            );
        }
    }