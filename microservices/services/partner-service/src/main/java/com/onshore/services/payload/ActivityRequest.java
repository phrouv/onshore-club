package com.onshore.services.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record ActivityRequest(
    @NotNull UUID partnerId,
    @NotBlank String name,
    @NotBlank String category,
    String description,
    @Min(1) Integer durationMinutes,
    @Min(0) BigDecimal partnerPayout,
    @Min(1) Integer defaultCredits,
    Boolean active) {}
