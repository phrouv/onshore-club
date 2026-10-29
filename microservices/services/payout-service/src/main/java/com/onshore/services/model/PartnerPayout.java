package com.onshore.services.model;

import com.onshore.common_lib.enums.PayoutStatus;
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
@Table(name = "partner_payouts")
public class PartnerPayout implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private UUID partnerId;

  @Column(nullable = false, unique = true)
  private UUID bookingId;

  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private PayoutStatus status = PayoutStatus.PENDING;

  private LocalDate paymentDate;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
