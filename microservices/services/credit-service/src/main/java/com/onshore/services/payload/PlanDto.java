package com.onshore.services.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PlanDto(
    @NotBlank String id,
    @NotBlank String name,
    @NotNull @Min(0) BigDecimal monthlyPrice,
    @NotNull @Min(1) Integer creditsPerMonth,
    String positioning,
    Boolean popular,
    Boolean active,
    Integer sortOrder) {}
