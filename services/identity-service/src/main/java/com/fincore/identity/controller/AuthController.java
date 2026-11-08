
package com.fincore.identity.controller;

import com.fincore.identity.dto.request.LoginRequest;
import com.fincore.identity.dto.request.RefreshTokenRequest;
import com.fincore.identity.dto.request.RegisterRequest;
import com.fincore.identity.dto.response.ApiResponse;
import com.fincore.identity.dto.response.LoginResponse;
import com.fincore.identity.dto.response.RefreshTokenResponse;
import com.fincore.identity.dto.response.RegisterResponse;
import com.fincore.identity.service.AuthService;
import com.fincore.identity.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "User registered successfully",
                        httpRequest.getRequestURI(),
                        response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        httpRequest.getRequestURI(),
                        response
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        RefreshTokenResponse response =
                authService.refreshAccessToken(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Access token refreshed successfully",
                        httpRequest.getRequestURI(),
                        response
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        refreshTokenService.revokeRefreshToken(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logged out successfully",
                        httpRequest.getRequestURI(),
                        null
                )
        );
    }
}
