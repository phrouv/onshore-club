package com.onshore.services.payload;

import com.onshore.common_lib.enums.DemandLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** demandLevel is only honoured for admins; recurrenceWeeks (1-12) repeats the class weekly. */
public record ClassRequest(
    @NotNull UUID activityId,
    @NotNull LocalDate date,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @NotNull @Min(1) Integer capacity,
    @NotNull @Min(0) Integer onePassCapacity,
    DemandLevel demandLevel,
    @Min(1) @Max(12) Integer recurrenceWeeks) {}
