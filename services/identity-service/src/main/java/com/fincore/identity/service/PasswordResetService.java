
package com.fincore.identity.service;

import com.fincore.identity.entity.User;

public interface PasswordResetService {

    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);

    void changePassword(
            User user,
            String currentPassword,
            String newPassword
    );
}
