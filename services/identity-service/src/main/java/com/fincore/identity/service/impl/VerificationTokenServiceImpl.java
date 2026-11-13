
package com.fincore.identity.service.impl;

import com.fincore.identity.entity.User;
import com.fincore.identity.entity.VerificationToken;
import com.fincore.identity.repository.VerificationTokenRepository;
import com.fincore.identity.service.VerificationTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public VerificationTokenServiceImpl(
            VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public String createVerificationToken(User user) {

        // Generate 32 cryptographically secure random bytes
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // Convert bytes into a URL-safe token
        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Hash the original token
        String tokenHash = hashToken(rawToken);

        // Create verification token entity
        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setUser(user);
        verificationToken.setTokenHash(tokenHash);
        verificationToken.setExpiresAt(
                Instant.now().plus(24, ChronoUnit.HOURS)
        );

        // Save only the hash in database
        verificationTokenRepository.save(verificationToken);

        // Return original token for email delivery
        return rawToken;
    }

    public String hashToken(String token) {

        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = messageDigest.digest(
                    token.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hashedBytes);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to hash verification token",
                    exception
            );
        }
    }
}
