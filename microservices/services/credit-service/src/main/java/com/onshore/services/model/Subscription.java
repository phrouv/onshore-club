package com.onshore.services.model;

import com.onshore.common_lib.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "subscriptions")
public class Subscription implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private String planId;

  @Column(nullable = false)
  private String planName;

  @Column(nullable = false)
  private BigDecimal monthlyPrice;

  @Column(nullable = false)
  private Integer creditsPerMonth;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

  @Column(nullable = false)
  private LocalDate startDate;

  private LocalDate endDate;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
