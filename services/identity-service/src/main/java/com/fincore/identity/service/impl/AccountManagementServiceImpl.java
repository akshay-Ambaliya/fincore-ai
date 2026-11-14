
package com.fincore.identity.service.impl;

import com.fincore.identity.entity.User;
import com.fincore.identity.exceptions.InvalidCurrentPasswordException;
import com.fincore.identity.repository.UserRepository;
import com.fincore.identity.service.AccountManagementService;
import com.fincore.identity.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountManagementServiceImpl
        implements AccountManagementService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void deactivateAccount(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        user.setEnabled(false);
        userRepository.save(user);

        // Revoke all refresh tokens
        refreshTokenService.revokeAllRefreshTokens(userId);
    }


    @Override
    @Transactional
    public void activateAccount(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        user.setEnabled(true);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void deleteAccount(User user, String currentPassword) {

        // 1. Verify current password
        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash())) {

            throw new InvalidCurrentPasswordException(
                    "Current password is incorrect"
            );
        }

        // 2. Delete the user
        userRepository.delete(user);
    }


}
