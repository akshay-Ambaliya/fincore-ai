
package com.fincore.identity.service.impl;

import com.fincore.identity.entity.PasswordResetTokens;
import com.fincore.identity.entity.User;
import com.fincore.identity.exceptions.InvalidCurrentPasswordException;
import com.fincore.identity.repository.PasswordResetTokenRepository;
import com.fincore.identity.repository.UserRepository;
import com.fincore.identity.service.PasswordResetService;
import com.fincore.identity.service.PasswordResetTokenService;
import com.fincore.identity.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        // Return silently if the email does not exist
        if (user == null || !user.isEmailVerified()) {
            return;
        }

        // Generate and store a new reset token
        String rawToken =
                passwordResetTokenService.createPasswordResetToken(user);

        // Build the reset link
        String resetLink =
                "http://localhost:5173/reset-password?token=" + rawToken;

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {

        String tokenHash =
                passwordResetTokenService.hashToken(rawToken);

        PasswordResetTokens resetToken =
                passwordResetTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired password reset token"
                                )
                        );

        // Check whether token was already used
        if (resetToken.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "Password reset token has already been used"
            );
        }

        // Check expiration
        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException(
                    "Password reset token has expired"
            );
        }

        User user = resetToken.getUser();

        // Encode and update the new password
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Mark token as used
        resetToken.setUsedAt(Instant.now());

        passwordResetTokenRepository.save(resetToken);

        // Revoke all existing refresh tokens
        refreshTokenService.revokeAllRefreshTokens(user.getId());
    }

    @Override
    public void changePassword(User user, String currentPassword, String newPassword) {
        //1. Verify the Current Password :

        if(!passwordEncoder.matches(currentPassword,user.getPasswordHash())){
            throw new InvalidCurrentPasswordException(
                    "Current password is incorrect"
            );
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        refreshTokenService.revokeAllRefreshTokens(user.getId());
    }
}
