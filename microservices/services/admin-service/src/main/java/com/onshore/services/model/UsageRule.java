package com.onshore.services.model;

import com.onshore.common_lib.enums.UsageScope;
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
@Table(name = "usage_rules")
public class UsageRule implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UsageScope scope;

  private UUID partnerId;

  private UUID activityId;

  private Integer maxUses;

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
