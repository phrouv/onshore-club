package com.onshore.services.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "credit_settings")
public class CreditSetting implements Serializable {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @Column(nullable = false)
  @Builder.Default
  private Integer creditsLow = 4;

  @Column(nullable = false)
  @Builder.Default
  private Integer creditsNormal = 5;

  @Column(nullable = false)
  @Builder.Default
  private Integer creditsHigh = 7;

  @Column(nullable = false)
  @Builder.Default
  private BigDecimal defaultPayout = new BigDecimal("5.00");

  private Instant updatedAt;
}
