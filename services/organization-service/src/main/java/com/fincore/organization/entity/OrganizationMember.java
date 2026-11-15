
package com.fincore.organization.entity;

import com.fincore.organization.enums.OrganizationMemberStatus;
import com.fincore.organization.enums.OrganizationRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "organization_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_organization_members_org_user",
                        columnNames = {"organization_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_organization_members_user_id",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
public class OrganizationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrganizationRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrganizationMemberStatus status =
            OrganizationMemberStatus.ACTIVE;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        joinedAt = now;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
