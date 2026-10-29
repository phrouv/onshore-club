package com.onshore.services.payload;

import com.onshore.common_lib.enums.UsageScope;
import java.time.Instant;
import java.util.UUID;

public record UsageRuleResponse(
    UUID id,
    UsageScope scope,
    UUID partnerId,
    UUID activityId,
    Integer maxUses,
    Boolean active,
    Instant createdAt) {}
