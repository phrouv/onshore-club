package com.onshore.common_lib.club;

import com.onshore.common_lib.enums.ClassStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ClassInfo(
    UUID id,
    UUID activityId,
    UUID partnerId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    int onePassCapacity,
    int creditsRequired,
    ClassStatus status) {}
