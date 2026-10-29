package com.onshore.services.model;

import jakarta.persistence.*;
import java.io.Serializable;
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
@Table(name = "app_settings")
public class AppSettings implements Serializable {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id;

  @Column(nullable = false)
  @Builder.Default
  private Integer checkinBeforeMinutes = 30;

  @Column(nullable = false)
  @Builder.Default
  private Integer checkinAfterMinutes = 15;

  @Column(nullable = false)
  @Builder.Default
  private Integer cancellationHours = 12;

  private Instant updatedAt;
}
