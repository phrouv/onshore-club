package com.onshore.services.model;

import com.onshore.common_lib.enums.PartnerStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "partners")
public class Partner implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String city;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false)
  private String category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private PartnerStatus status = PartnerStatus.PENDING;

  private Double latitude;

  private Double longitude;

  private String phone;

  private String professionalEmail;

  private String website;

  private String instagram;

  @Column(columnDefinition = "text")
  private String activityTypes;

  private String desiredSeats;

  private String managerFirstName;

  private String managerLastName;

  private String managerRole;

  @Column(nullable = false)
  private UUID ownerUserId;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
