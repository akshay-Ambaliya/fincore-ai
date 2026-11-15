
package com.fincore.organization.service;

import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.OrganizationResponse;
import com.fincore.organization.entity.OrganizationMember;
import com.fincore.organization.enums.OrganizationMemberStatus;

import java.util.List;

public interface OrganizationService {

    OrganizationResponse createOrganization(
            CreateOrganizationRequest request,
            Long userId
    );


    OrganizationResponse getOrganizationById(
            Long organizationId,
            Long userId
    );

    List<OrganizationResponse> getMyOrganizations(Long userId);

}
