
package com.fincore.identity.controller;

import com.fincore.identity.dto.response.ApiResponse;
import com.fincore.identity.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {

        Map<String, Object> response = Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User details fetched successfully",
                        httpRequest.getRequestURI(),
                        response
                )
        );
    }
}
