package com.onshore.services.model;

import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;

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

  @NotNull(message = "address is required")
  @Size(max = 500, message = "address must not exceed 500 characters")
  @Column(name = "address", length = 500, nullable = false)
  private String address;

  @NotNull(message = "city is required")
  @Size(max = 500, message = "city must not exceed 500 characters")
  @Column(name = "city", length = 500, nullable = false)
  private String city;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  @Builder.Default
  private PartnerStatus status = PartnerStatus.PENDING;

  @Size(max = 100, message = "phone must not exceed 100 characters")
  @Column(name = "phone", length = 100)
  private String phone;

  @Size(max = 500, message = "professionalEmail must not exceed 500 characters")
  @Column(name = "professional_email", length = 500)
  private String professionalEmail;

  @Size(max = 500, message = "website must not exceed 500 characters")
  @Column(name = "website", length = 500)
  private String website;

  @Size(max = 500, message = "instagram must not exceed 500 characters")
  @Column(name = "instagram", length = 500)
  private String instagram;

  @Column(name = "activity_types", columnDefinition = "text")
  private String activityTypes;

  @Size(max = 255, message = "desiredSeats must not exceed 255 characters")
  @Column(name = "desired_seats", length = 255)
  private String desiredSeats;

  @Size(max = 100, message = "managerFirstName must not exceed 100 characters")
  @Column(name = "manager_first_name", length = 100)
  private String managerFirstName;

  @Size(max = 100, message = "managerLastName must not exceed 100 characters")
  @Column(name = "manager_last_name", length = 100)
  private String managerLastName;

  @NotNull(message = "ownerUserId is required")
  @Column(name = "owner_user_id", nullable = false)
  private UUID ownerUserId;
}
