
package com.fincore.identity.controller;

import com.fincore.identity.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

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
}
