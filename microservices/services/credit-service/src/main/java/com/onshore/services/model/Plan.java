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
@Table(name = "plans")
public class Plan implements Serializable {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private BigDecimal monthlyPrice;

  @Column(nullable = false)
  private Integer creditsPerMonth;

  @Column(nullable = false)
  @Builder.Default
  private String positioning = "";

  @Column(nullable = false)
  @Builder.Default
  private Boolean popular = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  @Column(nullable = false)
  @Builder.Default
  private Integer sortOrder = 0;

  private Instant updatedAt;
}
