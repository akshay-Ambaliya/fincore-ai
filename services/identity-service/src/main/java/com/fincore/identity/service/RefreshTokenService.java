package com.fincore.identity.service;

import com.fincore.identity.entity.User;

public interface RefreshTokenService {

    String createRefreshToken(User user);

    User validateRefreshToken(String rawToken);

    void revokeRefreshToken(String rawToken);

    void revokeAllRefreshTokens(Long userId);
}