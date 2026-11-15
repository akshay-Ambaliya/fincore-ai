package com.fincore.organization.dto.response;

public record IdentityUserVerificationResponse(
        Long userId,
        boolean exists,
        boolean enabled
) {
}
