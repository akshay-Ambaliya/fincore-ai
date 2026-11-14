
package com.fincore.identity.controller;

import com.fincore.identity.dto.response.ApiResponse;
import com.fincore.identity.service.AccountManagementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountManagementService accountManagementService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAdminDashboard(
            HttpServletRequest httpRequest
    ) {

        Map<String, String> data = Map.of(
                "access", "granted",
                "role", "PLATFORM_ADMIN",
                "message", "Welcome to the Admin Dashboard"
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Admin dashboard fetched successfully",
                        httpRequest.getRequestURI(),
                        data
                )
        );
    }

    @PatchMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(
            @PathVariable Long userId,
            HttpServletRequest request) {

        accountManagementService.deactivateAccount(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User account deactivated successfully.",
                        request.getRequestURI(),
                        null
                )
        );
    }


    @PatchMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateAccount(
            @PathVariable Long userId,
            HttpServletRequest request) {

        accountManagementService.activateAccount(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User account activated successfully.",
                        request.getRequestURI(),
                        null
                )
        );
    }
}
