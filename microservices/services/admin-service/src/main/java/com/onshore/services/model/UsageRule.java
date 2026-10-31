package com.onshore.services.model;

import com.onshore.common_lib.enums.UsageScope;
import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "usage_rules")
public class UsageRule extends AbstractFullAuditingEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UsageScope scope;

  private UUID partnerId;

  private UUID activityId;

  private Integer maxUses;

  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;
}
