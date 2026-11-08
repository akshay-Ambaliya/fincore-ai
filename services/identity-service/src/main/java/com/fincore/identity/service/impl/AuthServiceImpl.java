package com.fincore.identity.service.impl;

import com.fincore.identity.dto.request.RefreshTokenRequest;
import com.fincore.identity.dto.request.RegisterRequest;
import com.fincore.identity.dto.response.RefreshTokenResponse;
import com.fincore.identity.dto.response.RegisterResponse;
import com.fincore.identity.entity.User;
import com.fincore.identity.exceptions.EmailAlreadyExistsException;
import com.fincore.identity.repository.UserRepository;
import com.fincore.identity.service.AuthService;
import com.fincore.identity.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.fincore.identity.dto.request.LoginRequest;
import com.fincore.identity.dto.response.LoginResponse;
import com.fincore.identity.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900L
        );
    }

    @Override
    public RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request) {

        User user = refreshTokenService.validateRefreshToken(
                request.getRefreshToken()
        );

        String accessToken = jwtService.generateAccessToken(user);

        return new RefreshTokenResponse(
                accessToken,
                "Bearer",
                900L
        );
    }
}