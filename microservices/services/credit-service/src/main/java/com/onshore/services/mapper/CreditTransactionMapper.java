package com.onshore.services.mapper;

import com.onshore.services.model.CreditTransaction;
import com.onshore.services.payload.CreditTransactionResponse;

public class CreditTransactionMapper {

  private CreditTransactionMapper() {}

  public static CreditTransactionResponse toResponse(CreditTransaction e) {
    return new CreditTransactionResponse(
        e.getId(),
        e.getUserId(),
        e.getAmount(),
        e.getType(),
        e.getDescription(),
        e.getPricePaid(),
        e.getCreatedAt());
  }
}
