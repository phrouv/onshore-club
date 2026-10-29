package com.onshore.services.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreditSettingDto(
    @NotBlank String category,
    @NotNull @Min(0) Integer creditsLow,
    @NotNull @Min(0) Integer creditsNormal,
    @NotNull @Min(0) Integer creditsHigh,
    @NotNull @Min(0) BigDecimal defaultPayout) {}
