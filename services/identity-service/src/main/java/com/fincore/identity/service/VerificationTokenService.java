package com.fincore.identity.service;

import com.fincore.identity.entity.User;

public interface VerificationTokenService {
    String createVerificationToken(User user);
    String hashToken(String token);
}
