package com.fincore.identity.service;

import com.fincore.identity.entity.User;

public interface EmailVerificationService {
    void sendVerificationEmail(User user);

}
