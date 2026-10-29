package com.onshore.services.repository;

import com.onshore.common_lib.enums.SubscriptionStatus;
import com.onshore.services.model.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  Optional<Subscription> findFirstByUserIdAndStatusOrderByStartDateDesc(
      UUID userId, SubscriptionStatus status);

  List<Subscription> findByStatusAndEndDateLessThanEqual(SubscriptionStatus status, LocalDate date);

  List<Subscription> findByStatus(SubscriptionStatus status);
}
