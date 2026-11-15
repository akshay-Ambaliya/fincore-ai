
package com.fincore.organization.repository;

import com.fincore.organization.entity.Organization;
import com.fincore.organization.entity.OrganizationMember;
import com.fincore.organization.enums.OrganizationMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
