package com.onshore.services.payload;

import jakarta.validation.constraints.NotBlank;

public record ChangePlanRequest(@NotBlank String planId) {}
