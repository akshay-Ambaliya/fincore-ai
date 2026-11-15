
package com.fincore.organization.dto.request;

import com.fincore.organization.enums.OrganizationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequest(

        @NotBlank(message = "Organization name is required")
        @Size(min = 2, max = 150, message = "Organization name must be between 2 and 150 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Organization type is required")
        OrganizationType organizationType

) {
}
