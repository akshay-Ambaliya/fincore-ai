
package com.fincore.identity.dto.response;

public record InternalUserVerificationResponse(
        Long userId,
        boolean exists,
        boolean enabled
) {
}
