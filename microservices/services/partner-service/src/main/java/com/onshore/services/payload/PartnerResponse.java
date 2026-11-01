package com.onshore.services.payload;

import com.onshore.common_lib.enums.PartnerStatus;
import java.time.Instant;
import java.util.UUID;

public record PartnerResponse(
    UUID id,
    String name,
    String street,
    String city,
    String zipCode,
    String description,
    PartnerStatus status,
    String website,
    String instagram,
    String managerFirstName,
    String managerLastName,
    String managerEmail,
    String managerPhoneNumber,
    UUID ownerUserId,
    Instant createdAt,
    Instant lastModifiedDate) {}
