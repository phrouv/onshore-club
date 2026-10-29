package com.onshore.services.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "activities")
public class Activity implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private UUID partnerId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String category;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false)
  @Builder.Default
  private Integer durationMinutes = 60;

  @Column(nullable = false)
  @Builder.Default
  private BigDecimal partnerPayout = new BigDecimal("5.00");

  private Integer defaultCredits;

  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
