package com.onshore.services.model;

import com.onshore.common_lib.enums.ActivityStatus;
import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import com.onshore.common_lib.model.ContactPerson;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
    name = "activities",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"club_id", "name"},
          name = "uk_activity_club_name")
    })
public class Activity extends AbstractFullAuditingEntity {

  @Column(name = "club_id", nullable = false)
  private UUID clubId;

  @Column(name = "category_id", nullable = false)
  private UUID categoryId;

  @NotNull(message = "name is required")
  @Size(max = 100, message = "name must not exceed 100 characters")
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @Embedded
  @EmbeddedColumnNaming("instructor_%s")
  private ContactPerson instructor;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private ActivityStatus status = ActivityStatus.ACTIVE;

  @Column(name = "subscription_date")
  private LocalDate subscriptionDate;
}
