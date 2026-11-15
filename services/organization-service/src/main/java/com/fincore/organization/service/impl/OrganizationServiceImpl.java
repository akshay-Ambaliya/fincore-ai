
package com.fincore.organization.service.impl;

import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.OrganizationResponse;
import com.fincore.organization.entity.Organization;
import com.fincore.organization.entity.OrganizationMember;
import com.fincore.organization.enums.OrganizationMemberStatus;
import com.fincore.organization.enums.OrganizationRole;
import com.fincore.organization.exception.OrganizationAccessDeniedException;
import com.fincore.organization.exception.OrganizationNotFoundException;
import com.fincore.organization.repository.OrganizationMemberRepository;
import com.fincore.organization.repository.OrganizationRepository;
import com.fincore.organization.service.OrganizationService;
import com.fincore.organization.service.OrganizationSlugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
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

        // Create organization
        Organization organization = new Organization();

        organization.setName(request.name());
        organization.setSlug(slug);
        organization.setDescription(request.description());
        organization.setOrganizationType(request.organizationType());
        organization.setCreatedByUserId(userId);

        Organization savedOrganization =
                organizationRepository.save(organization);

        // Create membership for the organization creator
        OrganizationMember member = new OrganizationMember();

        member.setOrganization(savedOrganization);
        member.setUserId(userId);
        member.setRole(OrganizationRole.OWNER);
        member.setStatus(OrganizationMemberStatus.ACTIVE);

        organizationMemberRepository.save(member);

        // Return organization response
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


    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(
            Long organizationId,
            Long userId
    ) {

        // 1. Check whether organization exists
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() ->
                        new OrganizationNotFoundException(
                                "Organization not found"
                        )
                );

        // 2. Check whether user is a member
        boolean isActiveMember = organizationMemberRepository
                .existsByOrganizationIdAndUserIdAndStatus(
                        organizationId,
                        userId,
                        OrganizationMemberStatus.ACTIVE
                );

        if (!isActiveMember) {
            throw new OrganizationAccessDeniedException(
                    "You are not an active member of this organization"
            );
        }

        // 3. Convert entity into response DTO
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getDescription(),
                organization.getOrganizationType(),
                organization.getStatus(),
                organization.getCreatedByUserId(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getMyOrganizations(Long userId) {

        List<OrganizationMember> memberships =
                organizationMemberRepository
                        .findAllByUserIdAndStatusAndOrganization_Status(
                                userId,
                                OrganizationMemberStatus.ACTIVE,
                                "ACTIVE"
                        );

        return memberships.stream()
                .map(member -> {
                    Organization organization = member.getOrganization();

                    return new OrganizationResponse(
                            organization.getId(),
                            organization.getName(),
                            organization.getSlug(),
                            organization.getDescription(),
                            organization.getOrganizationType(),
                            organization.getStatus(),
                            organization.getCreatedByUserId(),
                            organization.getCreatedAt(),
                            organization.getUpdatedAt()
                    );
                })
                .toList();
    }

}
