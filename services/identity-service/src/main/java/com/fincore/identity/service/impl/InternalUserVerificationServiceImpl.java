
package com.fincore.identity.service.impl;

import com.fincore.identity.dto.response.InternalUserVerificationResponse;
import com.fincore.identity.entity.User;
import com.fincore.identity.repository.UserRepository;
import com.fincore.identity.service.InternalUserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalUserVerificationServiceImpl
        implements InternalUserVerificationService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public InternalUserVerificationResponse verifyUser(Long userId) {

        return userRepository.findById(userId)
                .map(user -> new InternalUserVerificationResponse(
                        user.getId(),
                        true,
                        user.isEnabled()
                ))
                .orElseGet(() -> new InternalUserVerificationResponse(
                        userId,
                        false,
                        false
                ));
    }
}
