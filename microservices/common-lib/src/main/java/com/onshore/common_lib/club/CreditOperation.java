package com.onshore.common_lib.club;

import com.onshore.common_lib.enums.CreditTransactionType;
import java.util.UUID;

public record CreditOperation(
    UUID userId, int amount, CreditTransactionType type, String description) {}
