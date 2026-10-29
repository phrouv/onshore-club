package com.onshore.services.payload;

import java.math.BigDecimal;
import java.util.UUID;

/** Earnings of one partner for one month (month is null for the all-time total). */
public record PayoutSummary(
    UUID partnerId,
    String month,
    long count,
    BigDecimal pending,
    BigDecimal paid,
    BigDecimal total) {}
