package com.onshore.services.repository;

import com.onshore.common_lib.enums.PaymentStatus;
import com.onshore.services.model.Payment;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

  Optional<Payment> findByBookingId(UUID bookingId);

  Optional<Payment> findByTransactionId(String transactionId);

  List<Payment> findByStatus(PaymentStatus status);

  List<Payment> findByBookingIdIn(Collection<UUID> bookingIds);
}
