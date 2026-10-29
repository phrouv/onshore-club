package com.onshore.services.payload;

import com.onshore.common_lib.enums.PayoutStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PartnerPayoutResponse(
    UUID id,
    UUID partnerId,
    UUID bookingId,
    BigDecimal amount,
    PayoutStatus status,
    LocalDate paymentDate,
    Instant createdAt) {}
