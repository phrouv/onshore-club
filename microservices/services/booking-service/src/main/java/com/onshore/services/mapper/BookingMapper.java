package com.onshore.services.mapper;

import com.onshore.services.model.Booking;
import com.onshore.services.model.Checkin;
import com.onshore.services.payload.BookingResponse;

public class BookingMapper {

  private BookingMapper() {}

  public static String code(Booking b) {
    return b.getId().toString().substring(0, 8).toUpperCase();
  }

  public static BookingResponse toResponse(Booking b, Checkin checkin, boolean showCredits) {
    return new BookingResponse(
        b.getId(),
        code(b),
        b.getUserId(),
        b.getClassId(),
        b.getActivityId(),
        b.getPartnerId(),
        showCredits ? b.getCreditsSpent() : null,
        b.getStatus(),
        b.getClassDate(),
        b.getClassStartTime(),
        b.getCreatedAt(),
        checkin == null ? null : checkin.getCheckedInAt(),
        checkin == null ? null : checkin.getMethod());
  }
}
