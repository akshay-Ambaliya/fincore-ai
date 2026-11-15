
package com.fincore.identity.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class InternalServiceKeyValidator {

    private final String expectedApiKey;

    public InternalServiceKeyValidator(
            @Value("${internal.service.api-key}") String expectedApiKey
    ) {
        this.expectedApiKey = expectedApiKey;
    }

    public boolean isValid(String providedApiKey) {

        if (providedApiKey == null || providedApiKey.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                expectedApiKey.getBytes(StandardCharsets.UTF_8),
                providedApiKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}
