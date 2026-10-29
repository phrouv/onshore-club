package com.onshore.services.model;

import com.onshore.common_lib.enums.PaymentGateway;
import com.onshore.common_lib.enums.PaymentStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  // Cross-service ref: User (user-service)
  @Column(name = "user_id")
  private UUID userId;

  // Cross-service ref: Booking (booking-service)
  @Column(name = "booking_id")
  private UUID bookingId;

  private Double amount;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private PaymentGateway provider;

  private String providerPaymentId;
  private String transactionId;
  private String method;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private PaymentStatus status;

  private String failureReason;
  private LocalDateTime paidAt;
  private String refundId;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
