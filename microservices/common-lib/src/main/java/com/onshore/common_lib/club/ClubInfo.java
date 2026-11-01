package com.onshore.common_lib.club;

import java.math.BigDecimal;
import java.util.UUID;

public record ClubInfo(
    UUID id,
    UUID partnerId,
    String name,
    String city,
    BigDecimal latitude,
    BigDecimal longitude,
    boolean open) {}
