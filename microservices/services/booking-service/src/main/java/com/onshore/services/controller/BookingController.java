package com.onshore.services.controller;

import com.onshore.common_lib.club.BookingStats;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.common_lib.security.Roles;
import com.onshore.services.payload.BookRequest;
import com.onshore.services.payload.BookingResponse;
import com.onshore.services.payload.CodeRequest;
import com.onshore.services.service.BookingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  // ----- Member -----

  @PostMapping("/api/bookings")
  public ResponseEntity<BookingResponse> book(
      @Valid @RequestBody BookRequest request, @RequestHeader("X-User-Id") UUID userId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookingService.book(userId, request.classId()));
  }

  @GetMapping("/api/bookings/mine")
  public List<BookingResponse> mine(@RequestHeader("X-User-Id") UUID userId) {
    return bookingService.mine(userId);
  }

  @PostMapping("/api/bookings/{id}/cancel")
  public BookingResponse cancel(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
    return bookingService.cancel(userId, id);
  }

  @PostMapping("/api/bookings/{id}/checkin")
  public BookingResponse checkIn(
      @PathVariable UUID id,
      @Valid @RequestBody CodeRequest request,
      @RequestHeader("X-User-Id") UUID userId) {
    return bookingService.checkIn(userId, id, request.code());
  }

  // ----- Partner / admin -----

  @GetMapping("/api/bookings")
  public List<BookingResponse> list(
      @RequestParam(required = false) UUID partnerId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    requirePartnerOrAdmin(roles);
    return bookingService.list(partnerId, from, to, userId, roles);
  }

  @PostMapping("/api/bookings/validate")
  public BookingResponse validate(
      @Valid @RequestBody CodeRequest request,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    requirePartnerOrAdmin(roles);
    return bookingService.validateManually(userId, roles, request.code());
  }

  // ----- Service-to-service; never routed by the api-gateway -----

  @GetMapping("/internal/bookings/counts")
  public Map<UUID, Long> counts(@RequestParam List<UUID> classIds) {
    return bookingService.counts(classIds);
  }

  @GetMapping("/internal/stats")
  public BookingStats stats() {
    return bookingService.stats();
  }

  private static void requirePartnerOrAdmin(String roles) {
    if (!Roles.isAdmin(roles) && !Roles.has(roles, Roles.PARTNER)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "Partner or admin role required");
    }
  }
}
