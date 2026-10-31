package com.onshore.services.model;

import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import jakarta.persistence.*;
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
public class AppSettings extends AbstractFullAuditingEntity {

  @Column(nullable = false)
  @Builder.Default
  private Integer checkinBeforeMinutes = 30;

  @Column(nullable = false)
  @Builder.Default
  private Integer checkinAfterMinutes = 15;

  @Column(nullable = false)
  @Builder.Default
  private Integer cancellationHours = 12;
}
