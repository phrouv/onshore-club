package com.onshore.services.payload;

import com.onshore.common_lib.enums.CreditTransactionType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** amount may be negative; type defaults to ADMIN_ADJUSTMENT (PROMOTIONAL_CREDIT also accepted). */
public record AdjustRequest(
    @NotNull UUID userId,
    @NotNull Integer amount,
    String description,
    CreditTransactionType type) {}
