package com.onshore.services.payload;

import com.onshore.common_lib.enums.ClassStatus;
import com.onshore.common_lib.enums.DemandLevel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** demandLevel: admin only. creditsRequired: member and admin only. */
public record ClassResponse(
    UUID id,
    UUID activityId,
    UUID partnerId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    int capacity,
    int onePassCapacity,
    int seatsLeft,
    DemandLevel demandLevel,
    Integer creditsRequired,
    ClassStatus status) {}
