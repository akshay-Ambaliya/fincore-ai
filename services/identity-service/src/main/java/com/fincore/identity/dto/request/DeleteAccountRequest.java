package com.fincore.identity.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(

        @NotBlank(message = "Current password is required")
        String currentPassword

) {}
