package com.onshore.common_lib.club;

import java.math.BigDecimal;
import java.util.UUID;

public record ActivityInfo(
    UUID id,
    UUID partnerId,
    String name,
    String category,
    BigDecimal partnerPayout,
    Integer defaultCredits,
    boolean active) {}
