
package com.fincore.identity.service.impl;

import com.fincore.identity.entity.PasswordResetTokens;
import com.fincore.identity.entity.User;
import com.fincore.identity.repository.PasswordResetTokenRepository;
import com.fincore.identity.service.PasswordResetTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class PasswordResetTokenServiceImpl
        implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetTokenServiceImpl(
            PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }


    @Override
    @Transactional
    public String createPasswordResetToken(User user) {

        Instant now = Instant.now();

        // 1. Invalidate previous unused tokens
        List<PasswordResetTokens> previousTokens =
                passwordResetTokenRepository
                        .findAllByUser_IdAndUsedAtIsNull(user.getId());

        for (PasswordResetTokens previousToken : previousTokens) {
            previousToken.setUsedAt(now);
        }

        passwordResetTokenRepository.saveAll(previousTokens);

        // 2. Generate secure random token
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // 3. Hash the token
        String tokenHash = hashToken(rawToken);

        // 4. Create reset token entity
        PasswordResetTokens resetToken = new PasswordResetTokens();

        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(
                now.plus(15, ChronoUnit.MINUTES)
        );

        // 5. Save token hash
        passwordResetTokenRepository.save(resetToken);

        // 6. Return raw token for email
        return rawToken;
    }


    @Override
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
                    "Unable to hash password reset token",
                    exception
            );
        }
    }
}
