
package com.fincore.organization.dto.request;

import com.fincore.organization.enums.OrganizationRole;
import jakarta.validation.constraints.NotNull;

public record AddOrganizationMemberRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Organization role is required")
        OrganizationRole role

) {
}
