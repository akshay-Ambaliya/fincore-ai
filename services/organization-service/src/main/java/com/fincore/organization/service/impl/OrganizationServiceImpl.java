
package com.fincore.organization.service.impl;

import com.fincore.organization.client.IdentityServiceClient;
import com.fincore.organization.dto.request.AddOrganizationMemberRequest;
import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.IdentityUserVerificationResponse;
import com.fincore.organization.dto.response.OrganizationMemberResponse;
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
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationSlugService organizationSlugService;
    private final IdentityServiceClient identityServiceClient;

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


    @Override
    @Transactional
    public OrganizationMemberResponse addOrganizationMember(
            Long organizationId,
            Long requesterUserId,
            AddOrganizationMemberRequest request) {

        // 1. Find organization
        var organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "Organization not found"
                ));

        // 2. Check organization status
        if (!"ACTIVE".equals(organization.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot add members to an inactive organization"
            );
        }

        // 3. Find requester membership
        OrganizationMember requesterMembership =
                organizationMemberRepository
                        .findByOrganizationIdAndUserId(
                                organizationId,
                                requesterUserId
                        )
                        .orElseThrow(() -> new OrganizationAccessDeniedException(
                                "You are not a member of this organization"
                        ));

        // 4. Check requester membership status
        if (requesterMembership.getStatus() != OrganizationMemberStatus.ACTIVE) {
            throw new OrganizationAccessDeniedException(
                    "Your organization membership is not active"
            );
        }

        // 5. Check requester role
        OrganizationRole requesterRole = requesterMembership.getRole();

        if (requesterRole != OrganizationRole.OWNER
                && requesterRole != OrganizationRole.ADMIN) {

            throw new OrganizationAccessDeniedException(
                    "Only organization owners and admins can add members"
            );
        }

        // 6. Prevent assigning OWNER through this endpoint
        if (request.role() == OrganizationRole.OWNER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OWNER role cannot be assigned through this endpoint"
            );
        }

        // 7. Prevent duplicate membership
        if (organizationMemberRepository.existsByOrganizationIdAndUserId(
                organizationId,
                request.userId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already a member of this organization"
            );
        }

        // 8. Verify target user through Identity Service
        IdentityUserVerificationResponse verification;

        try {
            verification = identityServiceClient.verifyUser(request.userId());

        } catch (FeignException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to verify user through Identity Service",
                    exception
            );
        }

        // 9. Check whether target user exists
        if (!verification.exists()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Target user does not exist"
            );
        }

        // 10. Check whether target user is enabled
        if (!verification.enabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Target user account is disabled"
            );
        }

        // 11. Create membership
        OrganizationMember member = new OrganizationMember();

        member.setOrganization(organization);
        member.setUserId(request.userId());
        member.setRole(request.role());
        member.setStatus(OrganizationMemberStatus.ACTIVE);

        // 12. Save membership
        OrganizationMember savedMember =
                organizationMemberRepository.save(member);

        // 13. Return response
        return new OrganizationMemberResponse(
                savedMember.getId(),
                organization.getId(),
                savedMember.getUserId(),
                savedMember.getRole(),
                savedMember.getStatus(),
                savedMember.getJoinedAt()
        );
    }


}
