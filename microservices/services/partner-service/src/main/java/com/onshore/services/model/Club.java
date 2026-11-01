package com.onshore.services.model;

import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import com.onshore.common_lib.model.Address;
import com.onshore.common_lib.model.ContactPerson;
import com.onshore.common_lib.model.GeoLocation;
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
    name = "clubs",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"partner_id", "name"},
          name = "uk_club_partner_name")
    })
public class Club extends AbstractFullAuditingEntity {

  @NotNull(message = "partnerId is required")
  @Column(name = "partner_id", nullable = false)
  private UUID partnerId;

  @NotNull(message = "name is required")
  @Size(max = 100, message = "name must not exceed 100 characters")
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Embedded
  @EmbeddedColumnNaming("club_%s")
  private Address address;

  @Embedded
  @EmbeddedColumnNaming("club_%s")
  private GeoLocation geoLocation;

  @Embedded
  @EmbeddedColumnNaming("contact_%s")
  private ContactPerson contact;

  @Builder.Default
  @Column(name = "open", nullable = false)
  private boolean open = true;
}
