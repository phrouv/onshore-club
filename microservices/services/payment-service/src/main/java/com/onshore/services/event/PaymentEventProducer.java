package com.onshore.services.event;

import com.onshore.common_lib.event.PaymentCompletedEvent;
import com.onshore.common_lib.event.PaymentFailedEvent;
import com.onshore.services.model.Payment;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendPaymentCompleted(Payment payment) {
    PaymentCompletedEvent event =
        PaymentCompletedEvent.builder()
            .paymentId(payment.getId())
            .bookingId(payment.getBookingId())
            .userId(payment.getUserId())
            .amount(payment.getAmount())
            .transactionId(payment.getTransactionId())
            .providerPaymentId(payment.getProviderPaymentId())
            .paidAt(payment.getPaidAt())
            .build();

    kafkaTemplate.send("payment.completed", event);
    log.info(
        "Published PaymentCompletedEvent for payment ID: {}, booking ID: {}",
        payment.getId(),
        payment.getBookingId());
  }

  public void sendPaymentFailed(Payment payment) {
    PaymentFailedEvent event =
        PaymentFailedEvent.builder()
            .paymentId(payment.getId())
            .bookingId(payment.getBookingId())
            .userId(payment.getUserId())
            .amount(payment.getAmount())
            .transactionId(payment.getTransactionId())
            .failureReason(payment.getFailureReason())
            .failedAt(LocalDateTime.now())
            .build();

    kafkaTemplate.send("payment.failed", event);
    log.warn(
        "Published PaymentFailedEvent for payment ID: {} - Reason: {}",
        payment.getId(),
        payment.getFailureReason());
  }
}
