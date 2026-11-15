
package com.fincore.identity.controller;

import com.fincore.identity.dto.response.InternalUserVerificationResponse;
import com.fincore.identity.security.InternalServiceKeyValidator;
import com.fincore.identity.service.InternalUserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final InternalUserVerificationService verificationService;
    private final InternalServiceKeyValidator keyValidator;

    @GetMapping("/{userId}/verification")
    public ResponseEntity<?> verifyUser(
            @PathVariable("userId") Long userId,
            @RequestHeader(
                    value = "X-Internal-Service-Key",
                    required = false
            ) String apiKey
    ) {

        if (!keyValidator.isValid(apiKey)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid internal service credentials");
        }

        InternalUserVerificationResponse response =
                verificationService.verifyUser(userId);

        return ResponseEntity.ok(response);
    }
}
