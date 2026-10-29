package com.onshore.services.service;

import com.onshore.common_lib.club.BookingStats;
import com.onshore.services.payload.BookingResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BookingService {

  BookingResponse book(UUID userId, UUID classId);

  BookingResponse cancel(UUID userId, UUID bookingId);

  /** Member check-in by scanning the partner's QR code. */
  BookingResponse checkIn(UUID userId, UUID bookingId, String scannedCode);

  /** Fallback validation by the partner (or an admin) from the booking code. */
  BookingResponse validateManually(UUID actorId, String roles, String code);

  List<BookingResponse> mine(UUID userId);

  List<BookingResponse> list(
      UUID partnerId, LocalDate from, LocalDate to, UUID actorId, String roles);

  Map<UUID, Long> counts(List<UUID> classIds);

  BookingStats stats();

  void markNoShows();
}
