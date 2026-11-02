package com.onshore.services.payload;

import com.onshore.common_lib.enums.ActivityStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ActivityResponse(
    UUID id,
    UUID clubId,
    UUID categoryId,
    String name,
    String description,
    String instructorFirstName,
    String instructorLastName,
    String instructorEmail,
    String instructorPhoneNumber,
    ActivityStatus status,
    LocalDate subscriptionDate,
    Instant createdAt) {}
