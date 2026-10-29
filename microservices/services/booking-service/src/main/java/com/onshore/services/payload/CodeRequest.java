package com.onshore.services.payload;

import jakarta.validation.constraints.NotBlank;

/**
 * Scanned partner QR (ONEPASS:XXXXXXXX) for a member check-in, or a booking code for a manual
 * validation.
 */
public record CodeRequest(@NotBlank String code) {}
