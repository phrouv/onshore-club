package com.onshore.services.service;

import com.onshore.common_lib.club.CreditStats;
import com.onshore.common_lib.enums.CreditTransactionType;
import com.onshore.common_lib.enums.DemandLevel;
import com.onshore.services.payload.CreditTransactionResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreditService {

  int balance(UUID userId);

  List<CreditTransactionResponse> transactions(UUID userId);

  /**
   * Applies a signed amount; fails with INSUFFICIENT_CREDITS when the balance would go negative.
   */
  int apply(
      UUID userId,
      int amount,
      CreditTransactionType type,
      String description,
      BigDecimal pricePaid);

  int purchase(UUID userId, int credits, BigDecimal price);

  int price(String category, DemandLevel demand);

  CreditStats stats();
}
