package com.onshore.services.payload;

import com.onshore.common_lib.enums.PartnerStatus;
import java.time.Instant;
import java.util.UUID;

public record PartnerResponse(
    UUID id,
    String name,
    String address,
    String city,
    String description,
    PartnerStatus status,
    String phone,
    String professionalEmail,
    String website,
    String instagram,
    String activityTypes,
    String desiredSeats,
    String managerFirstName,
    String managerLastName,
    UUID ownerUserId,
    Instant createdAt,
    Instant lastModifiedDate) {}
