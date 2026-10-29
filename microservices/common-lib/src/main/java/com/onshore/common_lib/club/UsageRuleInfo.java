package com.onshore.common_lib.club;

import com.onshore.common_lib.enums.UsageScope;
import java.util.UUID;

public record UsageRuleInfo(UsageScope scope, UUID partnerId, UUID activityId, Integer maxUses) {}
