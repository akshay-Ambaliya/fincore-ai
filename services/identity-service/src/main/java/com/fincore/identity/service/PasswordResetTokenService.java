
package com.fincore.identity.service;

import com.fincore.identity.entity.User;

public interface PasswordResetTokenService {

    String createPasswordResetToken(User user);

    String hashToken(String token);
}
