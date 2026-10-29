package com.onshore.services.payload;

import com.onshore.common_lib.enums.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
    UUID id,
    UUID userId,
    String planId,
    String planName,
    BigDecimal monthlyPrice,
    Integer creditsPerMonth,
    SubscriptionStatus status,
    LocalDate startDate,
    LocalDate endDate,
    Instant createdAt) {}
