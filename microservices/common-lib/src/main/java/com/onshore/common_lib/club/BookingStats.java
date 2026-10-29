package com.onshore.common_lib.club;

public record BookingStats(
    long total,
    long confirmed,
    long completed,
    long noShows,
    long cancelled,
    long qrCheckins,
    long manualCheckins) {}
