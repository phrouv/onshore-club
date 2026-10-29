package com.onshore.services.model;

import com.onshore.common_lib.enums.SessionBookingStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
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
@Table(name = "bookings")
public class Booking implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private UUID classId;

  @Column(nullable = false)
  private UUID activityId;

  @Column(nullable = false)
  private UUID partnerId;

  @Column(nullable = false)
  @Builder.Default
  private Integer creditsSpent = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private SessionBookingStatus status = SessionBookingStatus.CONFIRMED;

  @Column(nullable = false)
  private LocalDate classDate;

  @Column(nullable = false)
  private LocalTime classStartTime;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
