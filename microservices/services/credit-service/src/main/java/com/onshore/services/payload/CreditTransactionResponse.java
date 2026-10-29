package com.onshore.services.payload;

import com.onshore.common_lib.enums.CreditTransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreditTransactionResponse(
    UUID id,
    UUID userId,
    Integer amount,
    CreditTransactionType type,
    String description,
    BigDecimal pricePaid,
    Instant createdAt) {}
