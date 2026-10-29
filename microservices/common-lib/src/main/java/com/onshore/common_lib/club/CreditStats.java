package com.onshore.common_lib.club;

import java.math.BigDecimal;

public record CreditStats(
    long activeSubscriptions,
    BigDecimal monthlyRecurringRevenue,
    BigDecimal purchaseRevenue,
    long creditsInCirculation) {}
