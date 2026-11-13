package com.fincore.identity.service.impl;

import com.fincore.identity.entity.User;
import com.fincore.identity.entity.VerificationToken;
import com.fincore.identity.repository.VerificationTokenRepository;
import com.fincore.identity.service.EmailVerificationService;
import com.fincore.identity.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final VerificationTokenService verificationTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    @Value("${app.verification.base-url}")
    private String verificationBaseUrl;

    public void sendVerificationEmail(User user) {

        String rawToken =
                verificationTokenService.createVerificationToken(user);

        String verificationLink =
                verificationBaseUrl + "?token=" + rawToken;

        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationLink
        );
    }

    @Transactional
    public void verifyEmail(String rawToken) {

        String tokenHash =
                verificationTokenService.hashToken(rawToken);

        VerificationToken verificationToken =
                verificationTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid verification token"
                                )
                        );

        if (verificationToken.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "Verification token has already been used"
            );
        }

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException(
                    "Verification token has expired"
            );
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        verificationToken.setUsedAt(Instant.now());
    }
}
