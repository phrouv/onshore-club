package com.onshore.common_lib.payload.request;

import com.onshore.common_lib.enums.PaymentGateway;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateRequest {

  @NotNull(message = "User ID is mandatory")
  private UUID userId;

  @NotNull(message = "bookingId is required")
  private UUID bookingId;

  @NotNull(message = "Payment gateway is mandatory")
  private PaymentGateway gateway;

  @NotNull(message = "Amount is mandatory")
  @Positive(message = "Amount must be positive")
  private Double amount;

  private String description;
}
