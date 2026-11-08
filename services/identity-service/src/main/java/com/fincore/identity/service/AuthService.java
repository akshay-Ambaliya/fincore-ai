package com.fincore.identity.service;

import com.fincore.identity.dto.request.LoginRequest;
import com.fincore.identity.dto.request.RefreshTokenRequest;
import com.fincore.identity.dto.request.RegisterRequest;
import com.fincore.identity.dto.response.LoginResponse;
import com.fincore.identity.dto.response.RefreshTokenResponse;
import com.fincore.identity.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);
}