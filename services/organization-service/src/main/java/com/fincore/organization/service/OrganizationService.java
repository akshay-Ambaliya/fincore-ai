
package com.fincore.organization.service;

import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.OrganizationResponse;

public interface OrganizationService {

    OrganizationResponse createOrganization(
            CreateOrganizationRequest request,
            Long userId
    );


    OrganizationResponse getOrganizationById(
            Long organizationId,
            Long userId
    );

}
