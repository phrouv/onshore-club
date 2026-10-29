package com.onshore.services.service;

import com.onshore.services.payload.SubscriptionResponse;
import java.util.UUID;

public interface SubscriptionService {

  SubscriptionResponse current(UUID userId);

  SubscriptionResponse changePlan(UUID userId, String planId);

  SubscriptionResponse cancel(UUID userId);

  /** Monthly renewal with capped rollover; run daily. */
  void renewDue();
}
