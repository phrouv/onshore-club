package com.onshore.common_lib.club;

public record AppSettingsInfo(
    int checkinBeforeMinutes, int checkinAfterMinutes, int cancellationHours) {}
