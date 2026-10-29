package com.onshore.services.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SettingsRequest(
    @NotNull @Min(0) Integer checkinBeforeMinutes,
    @NotNull @Min(0) Integer checkinAfterMinutes,
    @NotNull @Min(0) Integer cancellationHours) {}
