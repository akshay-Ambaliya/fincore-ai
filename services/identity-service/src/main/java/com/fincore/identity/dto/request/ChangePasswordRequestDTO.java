package com.fincore.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Current password is required")
    String currentPassword;

    @NotBlank(message = "New Password is required")
    @Size(min = 8, max=72, message = "New Password must be between 8 and 72 charatcer")
    String newPassword;

}
