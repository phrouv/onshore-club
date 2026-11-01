package com.onshore.services.model;

import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import com.onshore.common_lib.model.Address;
import com.onshore.common_lib.model.ContactPerson;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.EmbeddedColumnNaming;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "partners",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"name"},
          name = "uk_partner_name")
    })
public class Partner extends AbstractFullAuditingEntity {

  @NotNull(message = "name is required")
  @Size(max = 100, message = "name must not exceed 100 characters")
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Embedded
  @EmbeddedColumnNaming("partner_%s")
  private Address address;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private PartnerStatus status = PartnerStatus.PENDING;

  @Size(max = 500, message = "website must not exceed 500 characters")
  @Column(name = "website", length = 500)
  private String website;

  @Size(max = 500, message = "instagram must not exceed 500 characters")
  @Column(name = "instagram", length = 500)
  private String instagram;

  @Embedded
  @EmbeddedColumnNaming("manager_%s")
  private ContactPerson manager;

  @NotNull(message = "ownerUserId is required")
  @Column(name = "owner_user_id", nullable = false)
  private UUID ownerUserId;
}
