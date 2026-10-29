package com.onshore.common_lib.payload.response;

import com.onshore.common_lib.enums.PaymentGateway;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateResponse {

  private UUID paymentId;
  private PaymentGateway gateway;
  private String transactionId;

  // Razorpay specific fields
  private String razorpayOrderId;

  private Double amount;
  private String currency;
  private String description;

  // Frontend should redirect user to this URL for payment
  private String checkoutUrl;

  private String message;
  private Boolean success;
}
