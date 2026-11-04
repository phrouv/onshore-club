package com.onshore.common_lib.club;

import com.onshore.common_lib.enums.ActivityStatus;
import java.util.UUID;

public record ActivityInfo(
    UUID id, UUID clubId, UUID partnerId, String name, UUID categoryId, ActivityStatus status) {}
