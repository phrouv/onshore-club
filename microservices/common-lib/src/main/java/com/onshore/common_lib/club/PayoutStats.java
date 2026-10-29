package com.onshore.common_lib.club;

import java.math.BigDecimal;

public record PayoutStats(
    long count, BigDecimal totalAmount, BigDecimal pendingAmount, BigDecimal paidAmount) {}
