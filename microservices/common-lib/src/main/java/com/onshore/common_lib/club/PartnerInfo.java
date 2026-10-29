package com.onshore.common_lib.club;

import com.onshore.common_lib.enums.PartnerStatus;
import java.util.UUID;

public record PartnerInfo(UUID id, String name, UUID ownerUserId, PartnerStatus status) {}
