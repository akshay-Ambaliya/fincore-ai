
package com.fincore.organization.dto.response;

import com.fincore.organization.enums.OrganizationType;

import java.time.Instant;

public record OrganizationResponse(

        Long id,
        String name,
        String slug,
        String description,
        OrganizationType organizationType,
        String status,
        Long createdByUserId,
        Instant createdAt,
        Instant updatedAt

) {
}
