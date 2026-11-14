
package com.fincore.identity.service.impl;

import com.fincore.identity.entity.PasswordResetTokens;
import com.fincore.identity.entity.User;
import com.fincore.identity.exceptions.InvalidCurrentPasswordException;
import com.fincore.identity.exceptions.InvalidPasswordResetTokenException;
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
import java.util.List;

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

        String tokenHash = passwordResetTokenService.hashToken(rawToken);

        PasswordResetTokens resetToken =
                passwordResetTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new InvalidPasswordResetTokenException(
                                        "Invalid or expired password reset token"
                                )
                        );

        Instant now = Instant.now();

        if (resetToken.getUsedAt() != null ||
                resetToken.getExpiresAt().isBefore(now)) {

            throw new InvalidPasswordResetTokenException(
                    "Invalid or expired password reset token"
            );
        }

        User user = resetToken.getUser();

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Invalidate all outstanding reset tokens
        List<PasswordResetTokens> unusedTokens =
                passwordResetTokenRepository
                        .findAllByUser_IdAndUsedAtIsNull(user.getId());

        for (PasswordResetTokens token : unusedTokens) {
            token.setUsedAt(now);
        }

        passwordResetTokenRepository.saveAll(unusedTokens);

        // Revoke all refresh tokens
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
