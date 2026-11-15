
package com.fincore.organization.service.impl;

import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.OrganizationResponse;
import com.fincore.organization.entity.Organization;
import com.fincore.organization.repository.OrganizationRepository;
import com.fincore.organization.service.OrganizationService;
import com.fincore.organization.service.OrganizationSlugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationSlugService organizationSlugService;

    @Override
    @Transactional
    public OrganizationResponse createOrganization(
            CreateOrganizationRequest request,
            Long userId
    ) {

        // Generate unique organization slug
        String slug = organizationSlugService.generateUniqueSlug(
                request.name()
        );

        // Create organization entity
        Organization organization = new Organization();

        organization.setName(request.name());
        organization.setSlug(slug);
        organization.setDescription(request.description());
        organization.setOrganizationType(request.organizationType());
        organization.setCreatedByUserId(userId);

        // Save organization
        Organization savedOrganization =
                organizationRepository.save(organization);

        // Convert entity into response DTO
        return new OrganizationResponse(
                savedOrganization.getId(),
                savedOrganization.getName(),
                savedOrganization.getSlug(),
                savedOrganization.getDescription(),
                savedOrganization.getOrganizationType(),
                savedOrganization.getStatus(),
                savedOrganization.getCreatedByUserId(),
                savedOrganization.getCreatedAt(),
                savedOrganization.getUpdatedAt()
        );
    }
}
