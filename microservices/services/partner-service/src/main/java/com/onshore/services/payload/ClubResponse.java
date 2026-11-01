package com.onshore.services.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClubResponse(
    UUID id,
    UUID partnerId,
    String name,
    String description,
    String street,
    String city,
    String zipCode,
    BigDecimal latitude,
    BigDecimal longitude,
    String contactFirstName,
    String contactLastName,
    String contactEmail,
    String contactPhoneNumber,
    boolean open,
    Instant createdAt) {}
