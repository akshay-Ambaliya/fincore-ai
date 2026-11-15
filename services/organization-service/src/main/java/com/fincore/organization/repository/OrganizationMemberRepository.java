
package com.fincore.organization.repository;

import com.fincore.organization.entity.OrganizationMember;
import com.fincore.organization.enums.OrganizationMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMember, Long> {

    boolean existsByOrganizationIdAndUserIdAndStatus(
            Long organizationId,
            Long userId,
            OrganizationMemberStatus status
    );

    Optional<OrganizationMember> findByOrganizationIdAndUserId(
            Long organizationId,
            Long userId
    );

    List<OrganizationMember> findAllByOrganizationId(
            Long organizationId
    );

    List<OrganizationMember> findAllByUserId(Long userId);

    boolean existsByOrganizationIdAndUserId(
            Long organizationId,
            Long userId
    );


    List<OrganizationMember> findAllByUserIdAndStatusAndOrganization_Status(
            Long userId,
            OrganizationMemberStatus status,
            String organizationStatus
    );

}
