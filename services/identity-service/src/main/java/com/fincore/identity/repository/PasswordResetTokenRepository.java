
package com.fincore.identity.repository;

import com.fincore.identity.entity.PasswordResetTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetTokens, Long> {

    Optional<PasswordResetTokens> findByTokenHash(String tokenHash);

    List<PasswordResetTokens> findAllByUser_IdAndUsedAtIsNull(Long userId);


}
