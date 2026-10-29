package com.onshore.services.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** partnerPayout is null for members: it is never shown outside partner/admin spaces. */
public record ActivityResponse(
    UUID id,
    UUID partnerId,
    String name,
    String category,
    String description,
    Integer durationMinutes,
    BigDecimal partnerPayout,
    Integer defaultCredits,
    boolean active,
    Instant createdAt) {}
