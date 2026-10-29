package com.onshore.common_lib.club;

import java.math.BigDecimal;
import java.util.UUID;

public record PayoutCreateRequest(UUID partnerId, UUID bookingId, BigDecimal amount) {}
