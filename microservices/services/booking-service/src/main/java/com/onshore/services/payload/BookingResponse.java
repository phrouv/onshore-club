package com.onshore.services.payload;

import com.onshore.common_lib.enums.CheckinMethod;
import com.onshore.common_lib.enums.SessionBookingStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** creditsSpent is null for partners: member-side pricing is never shown to them. */
public record BookingResponse(
    UUID id,
    String code,
    UUID userId,
    UUID classId,
    UUID activityId,
    UUID partnerId,
    Integer creditsSpent,
    SessionBookingStatus status,
    LocalDate classDate,
    LocalTime classStartTime,
    Instant bookedAt,
    Instant checkedInAt,
    CheckinMethod checkinMethod) {}
