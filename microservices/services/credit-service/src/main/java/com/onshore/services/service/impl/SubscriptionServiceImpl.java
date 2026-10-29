package com.onshore.services.service.impl;

import com.onshore.common_lib.enums.CreditTransactionType;
import com.onshore.common_lib.enums.SubscriptionStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.mapper.SubscriptionMapper;
import com.onshore.services.model.Plan;
import com.onshore.services.model.Subscription;
import com.onshore.services.payload.SubscriptionResponse;
import com.onshore.services.repository.PlanRepository;
import com.onshore.services.repository.SubscriptionRepository;
import com.onshore.services.service.CreditService;
import com.onshore.services.service.SubscriptionService;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

  private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

  private final SubscriptionRepository subscriptionRepository;
  private final PlanRepository planRepository;
  private final CreditService creditService;

  @Override
  @Transactional(readOnly = true)
  public SubscriptionResponse current(UUID userId) {
    return SubscriptionMapper.toResponse(active(userId));
  }

  @Override
  public SubscriptionResponse changePlan(UUID userId, String planId) {
    Plan plan =
        planRepository
            .findById(planId)
            .filter(Plan::getActive)
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.PLAN_NOT_FOUND, "Plan not found: " + planId));
    LocalDate today = LocalDate.now();
    Subscription sub =
        subscriptionRepository
            .findFirstByUserIdAndStatusOrderByStartDateDesc(userId, SubscriptionStatus.ACTIVE)
            .orElse(null);
    int previousCredits = sub == null ? 0 : sub.getCreditsPerMonth();
    if (sub == null) {
      sub = new Subscription();
      sub.setUserId(userId);
    }
    sub.setPlanId(plan.getId());
    sub.setPlanName(plan.getName());
    sub.setMonthlyPrice(plan.getMonthlyPrice());
    sub.setCreditsPerMonth(plan.getCreditsPerMonth());
    sub.setStatus(SubscriptionStatus.ACTIVE);
    sub.setStartDate(today);
    sub.setEndDate(today.plusMonths(1));
    Subscription saved = subscriptionRepository.save(sub);

    // A new subscription grants the monthly credits; an upgrade grants the difference.
    int grant = plan.getCreditsPerMonth() - previousCredits;
    if (grant > 0) {
      creditService.apply(
          userId,
          grant,
          CreditTransactionType.MONTHLY_SUBSCRIPTION,
          "Plan " + plan.getName(),
          null);
    }
    return SubscriptionMapper.toResponse(saved);
  }

  /** The subscription stays active until its end date, then is no longer renewed. */
  @Override
  public SubscriptionResponse cancel(UUID userId) {
    Subscription sub = active(userId);
    sub.setStatus(SubscriptionStatus.CANCELLED);
    return SubscriptionMapper.toResponse(subscriptionRepository.save(sub));
  }

  @Override
  @Scheduled(cron = "0 0 3 * * *")
  public void renewDue() {
    for (Subscription sub :
        subscriptionRepository.findByStatusAndEndDateLessThanEqual(
            SubscriptionStatus.ACTIVE, LocalDate.now())) {
      // Rollover is capped at one month of the plan: the excess expires before the new grant.
      int excess = creditService.balance(sub.getUserId()) - sub.getCreditsPerMonth();
      if (excess > 0) {
        creditService.apply(
            sub.getUserId(),
            -excess,
            CreditTransactionType.CREDIT_EXPIRATION,
            "Rollover cap",
            null);
      }
      creditService.apply(
          sub.getUserId(),
          sub.getCreditsPerMonth(),
          CreditTransactionType.MONTHLY_SUBSCRIPTION,
          "Monthly renewal " + sub.getPlanName(),
          null);
      sub.setStartDate(sub.getEndDate());
      sub.setEndDate(sub.getEndDate().plusMonths(1));
      subscriptionRepository.save(sub);
      log.info("Renewed subscription {} for user {}", sub.getId(), sub.getUserId());
    }
  }

  private Subscription active(UUID userId) {
    return subscriptionRepository
        .findFirstByUserIdAndStatusOrderByStartDateDesc(userId, SubscriptionStatus.ACTIVE)
        .orElseThrow(
            () ->
                new ApiException(
                    ClubExceptionType.SUBSCRIPTION_NOT_FOUND,
                    "No active subscription for " + userId));
  }
}
