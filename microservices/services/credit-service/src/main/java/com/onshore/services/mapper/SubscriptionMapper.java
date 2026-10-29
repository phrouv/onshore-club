package com.onshore.services.mapper;

import com.onshore.services.model.Subscription;
import com.onshore.services.payload.SubscriptionResponse;

public class SubscriptionMapper {

  private SubscriptionMapper() {}

  public static SubscriptionResponse toResponse(Subscription e) {
    return new SubscriptionResponse(
        e.getId(),
        e.getUserId(),
        e.getPlanId(),
        e.getPlanName(),
        e.getMonthlyPrice(),
        e.getCreditsPerMonth(),
        e.getStatus(),
        e.getStartDate(),
        e.getEndDate(),
        e.getCreatedAt());
  }
}
