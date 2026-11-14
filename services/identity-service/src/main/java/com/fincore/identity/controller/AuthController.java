
package com.fincore.identity.controller;

import com.fincore.identity.dto.request.*;
import com.fincore.identity.dto.response.ApiResponse;
import com.fincore.identity.dto.response.LoginResponse;
import com.fincore.identity.dto.response.RefreshTokenResponse;
import com.fincore.identity.dto.response.RegisterResponse;
import com.fincore.identity.entity.User;
import com.fincore.identity.service.AuthService;
import com.fincore.identity.service.PasswordResetService;
import com.fincore.identity.service.RefreshTokenService;

import com.fincore.identity.service.impl.EmailService;
import com.fincore.identity.service.impl.EmailVerificationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final EmailVerificationServiceImpl emailVerificationService;
    private final PasswordResetService passwordResetService;

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


    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest) {

        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Email verified successfully",
                        httpRequest.getRequestURI(),
                        null
                )
        );
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam("email") String email) {

        System.out.println("STEP 1: Test email endpoint reached");

        System.out.println("STEP 2: About to call EmailService");

        emailService.sendTestEmail(email);

        System.out.println("STEP 3: EmailService completed");

        return ResponseEntity.ok("Email sent successfully");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println(">>> AUTH CONTROLLER REACHED <<<");
        return ResponseEntity.ok("Auth Controller is working");
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        passwordResetService.forgotPassword(request.email());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "If an account exists with this email, a password reset link has been sent.",
                        httpRequest.getRequestURI(),
                        null
                )
        );
    }




    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        passwordResetService.resetPassword(
                request.token(),
                request.newPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password has been reset successfully. Please log in again.",
                        httpRequest.getRequestURI(),
                        null
                )
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO request,
            Authentication authentication,HttpServletRequest httpServletRequest) {

        User user = (User) authentication.getPrincipal();
        passwordResetService.changePassword(
                user, request.getCurrentPassword(),request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password changed Successfully. Please Log in again",
                        httpServletRequest.getRequestURI(),
                        null
                )
        );

    }

}
