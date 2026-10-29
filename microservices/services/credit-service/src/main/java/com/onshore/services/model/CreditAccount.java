package com.onshore.services.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_accounts")
public class CreditAccount implements Serializable {

  @Id
  @Column(name = "user_id", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID userId;

  @Column(nullable = false)
  @Builder.Default
  private Integer balance = 0;

  private Instant updatedAt;
}
