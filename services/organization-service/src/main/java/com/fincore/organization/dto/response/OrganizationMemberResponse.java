
package com.fincore.organization.dto.response;

import com.fincore.organization.enums.OrganizationMemberStatus;
import com.fincore.organization.enums.OrganizationRole;

import java.time.Instant;

public record OrganizationMemberResponse(
        Long id,
        Long organizationId,
        Long userId,
        OrganizationRole role,
        OrganizationMemberStatus status,
        Instant joinedAt
) {
}
