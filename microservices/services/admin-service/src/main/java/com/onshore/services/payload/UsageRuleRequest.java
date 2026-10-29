package com.onshore.services.payload;

import com.onshore.common_lib.enums.UsageScope;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UsageRuleRequest(
    @NotNull UsageScope scope, UUID partnerId, UUID activityId, Integer maxUses, Boolean active) {}
