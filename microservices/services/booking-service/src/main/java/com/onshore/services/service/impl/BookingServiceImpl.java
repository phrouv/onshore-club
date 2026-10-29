package com.onshore.services.service.impl;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.common_lib.club.AppSettingsInfo;
import com.onshore.common_lib.club.BookingStats;
import com.onshore.common_lib.club.ClassInfo;
import com.onshore.common_lib.club.CreditOperation;
import com.onshore.common_lib.club.PartnerInfo;
import com.onshore.common_lib.club.PayoutCreateRequest;
import com.onshore.common_lib.club.UsageRuleInfo;
import com.onshore.common_lib.enums.CheckinMethod;
import com.onshore.common_lib.enums.ClassStatus;
import com.onshore.common_lib.enums.CreditTransactionType;
import com.onshore.common_lib.enums.SessionBookingStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.common_lib.security.Roles;
import com.onshore.services.client.AdminClient;
import com.onshore.services.client.CreditClient;
import com.onshore.services.client.PartnerClient;
import com.onshore.services.client.PayoutClient;
import com.onshore.services.client.PlanningClient;
import com.onshore.services.mapper.BookingMapper;
import com.onshore.services.model.Booking;
import com.onshore.services.model.Checkin;
import com.onshore.services.payload.BookingResponse;
import com.onshore.services.repository.BookingRepository;
import com.onshore.services.repository.CheckinRepository;
import com.onshore.services.service.BookingService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

  private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

  /** Members can only book classes starting within this many days. */
  static final int BOOKING_WINDOW_DAYS = 8;

  /** Statuses that occupy a One Pass seat (a no-show still consumed it). */
  private static final Set<SessionBookingStatus> SEAT_STATUSES =
      EnumSet.of(
          SessionBookingStatus.CONFIRMED,
          SessionBookingStatus.COMPLETED,
          SessionBookingStatus.NO_SHOW);

  private final BookingRepository bookingRepository;
  private final CheckinRepository checkinRepository;
  private final PlanningClient planningClient;
  private final PartnerClient partnerClient;
  private final CreditClient creditClient;
  private final AdminClient adminClient;
  private final PayoutClient payoutClient;

  // ---------- Booking ----------

  @Override
  public BookingResponse book(UUID userId, UUID classId) {
    ClassInfo klass = planningClient.classInfo(classId);
    LocalDate today = LocalDate.now();
    if (klass.status() == ClassStatus.CLOSED) {
      throw new ApiException(ClubExceptionType.CLASS_CLOSED, "This class is closed to bookings");
    }
    if (klass.date().isBefore(today)) {
      throw new ApiException(ClubExceptionType.CLASS_PAST, "This class is already past");
    }
    if (klass.date().isAfter(today.plusDays(BOOKING_WINDOW_DAYS))) {
      throw new ApiException(
          ClubExceptionType.BOOKING_WINDOW_EXCEEDED,
          "Bookings open up to " + BOOKING_WINDOW_DAYS + " days in advance only");
    }
    if (bookingRepository.countByClassIdAndStatusIn(classId, SEAT_STATUSES)
        >= klass.onePassCapacity()) {
      throw new ApiException(ClubExceptionType.CLASS_FULL, "No One Pass seat left on this class");
    }
    if (bookingRepository.existsByUserIdAndClassIdAndStatus(
        userId, classId, SessionBookingStatus.CONFIRMED)) {
      throw new ApiException(ClubExceptionType.ALREADY_BOOKED, "You already booked this class");
    }
    checkUsageRules(userId, klass);

    ActivityInfo activity = partnerClient.activity(klass.activityId());
    // Debit first: it fails with INSUFFICIENT_CREDITS before anything is stored.
    creditClient.apply(
        new CreditOperation(
            userId,
            -klass.creditsRequired(),
            CreditTransactionType.BOOKING,
            "Booking " + activity.name() + " - " + klass.date()));
    try {
      Booking saved =
          bookingRepository.save(
              Booking.builder()
                  .userId(userId)
                  .classId(classId)
                  .activityId(klass.activityId())
                  .partnerId(klass.partnerId())
                  .creditsSpent(klass.creditsRequired())
                  .classDate(klass.date())
                  .classStartTime(klass.startTime())
                  .build());
      return toResponse(saved, null, true);
    } catch (RuntimeException ex) {
      creditClient.apply(
          new CreditOperation(
              userId,
              klass.creditsRequired(),
              CreditTransactionType.CANCELLATION_REFUND,
              "Booking failed: refund"));
      throw ex;
    }
  }

  private void checkUsageRules(UUID userId, ClassInfo klass) {
    List<UsageRuleInfo> rules = adminClient.usageRules();
    if (rules.isEmpty()) return;
    List<Booking> mine = bookingRepository.findByUserIdAndStatusIn(userId, SEAT_STATUSES);
    YearMonth month = YearMonth.from(klass.date());
    for (UsageRuleInfo rule : rules) {
      long used =
          switch (rule.scope()) {
            case SESSIONS_PER_DAY ->
                mine.stream().filter(b -> b.getClassDate().equals(klass.date())).count();
            case ACTIVITY_PER_MONTH ->
                klass.activityId().equals(rule.activityId())
                    ? mine.stream()
                        .filter(
                            b ->
                                b.getActivityId().equals(klass.activityId())
                                    && YearMonth.from(b.getClassDate()).equals(month))
                        .count()
                    : -1;
            case PARTNER_PER_MONTH ->
                klass.partnerId().equals(rule.partnerId())
                    ? mine.stream()
                        .filter(
                            b ->
                                b.getPartnerId().equals(klass.partnerId())
                                    && YearMonth.from(b.getClassDate()).equals(month))
                        .count()
                    : -1;
          };
      if (used >= 0 && used + 1 > rule.maxUses()) {
        throw new ApiException(
            ClubExceptionType.USAGE_LIMIT_REACHED,
            "Limit reached: " + rule.maxUses() + " session(s) max for rule " + rule.scope());
      }
    }
  }

  // ---------- Cancellation ----------

  @Override
  public BookingResponse cancel(UUID userId, UUID bookingId) {
    Booking booking = ownedBooking(userId, bookingId);
    requireConfirmed(booking);
    AppSettingsInfo settings = adminClient.settings();
    LocalDateTime start = LocalDateTime.of(booking.getClassDate(), booking.getClassStartTime());
    boolean refundable =
        !LocalDateTime.now().isAfter(start.minusHours(settings.cancellationHours()));

    booking.setStatus(SessionBookingStatus.CANCELLED);
    bookingRepository.save(booking);
    if (refundable && booking.getCreditsSpent() > 0) {
      creditClient.apply(
          new CreditOperation(
              userId,
              booking.getCreditsSpent(),
              CreditTransactionType.CANCELLATION_REFUND,
              "Cancelled in time - " + booking.getClassDate()));
    } else {
      creditClient.apply(
          new CreditOperation(
              userId,
              0,
              CreditTransactionType.CANCELLATION_REFUND,
              "Late cancellation (less than "
                  + settings.cancellationHours()
                  + "h before the class): credits not refunded"));
    }
    return toResponse(booking, null, true);
  }

  // ---------- Check-in ----------

  @Override
  public BookingResponse checkIn(UUID userId, UUID bookingId, String scannedCode) {
    Booking booking = ownedBooking(userId, bookingId);
    requireNotCheckedIn(booking);
    requireConfirmed(booking);
    if (!partnerCode(booking.getPartnerId()).equals(readCode(scannedCode))) {
      throw new ApiException(
          ClubExceptionType.WRONG_PARTNER_CODE, "This QR code is not the one of your booked club");
    }
    AppSettingsInfo settings = adminClient.settings();
    LocalDateTime start = LocalDateTime.of(booking.getClassDate(), booking.getClassStartTime());
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(start.minusMinutes(settings.checkinBeforeMinutes()))) {
      throw new ApiException(
          ClubExceptionType.CHECKIN_WINDOW_NOT_OPEN,
          "Check-in opens " + settings.checkinBeforeMinutes() + " min before the class");
    }
    if (now.isAfter(start.plusMinutes(settings.checkinAfterMinutes()))) {
      throw new ApiException(
          ClubExceptionType.CHECKIN_WINDOW_CLOSED, "The check-in window of this class is closed");
    }
    return complete(booking, CheckinMethod.QR_CODE, "Member (QR)");
  }

  @Override
  public BookingResponse validateManually(UUID actorId, String roles, String code) {
    String cleaned = readCode(code);
    Booking booking =
        bookingRepository.findByCode(cleaned).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.BOOKING_NOT_FOUND, "No booking matches this code"));
    authorizePartner(booking.getPartnerId(), actorId, roles);
    requireNotCheckedIn(booking);
    requireConfirmed(booking);
    return complete(booking, CheckinMethod.MANUAL_PARTNER, actorId.toString());
  }

  /**
   * Records the check-in and creates the payout: always the fixed activity payout, never a function
   * of credits.
   */
  private BookingResponse complete(Booking booking, CheckinMethod method, String validatedBy) {
    ActivityInfo activity = partnerClient.activity(booking.getActivityId());
    Checkin checkin =
        checkinRepository.save(
            Checkin.builder()
                .bookingId(booking.getId())
                .checkedInAt(Instant.now())
                .validatedBy(validatedBy)
                .method(method)
                .build());
    booking.setStatus(SessionBookingStatus.COMPLETED);
    bookingRepository.save(booking);
    payoutClient.create(
        new PayoutCreateRequest(booking.getPartnerId(), booking.getId(), activity.partnerPayout()));
    return toResponse(booking, checkin, true);
  }

  // ---------- Queries ----------

  @Override
  @Transactional(readOnly = true)
  public List<BookingResponse> mine(UUID userId) {
    return toResponses(
        bookingRepository.findByUserIdOrderByClassDateDescClassStartTimeDesc(userId), true);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookingResponse> list(
      UUID partnerId, LocalDate from, LocalDate to, UUID actorId, String roles) {
    boolean admin = Roles.isAdmin(roles);
    if (!admin) {
      if (partnerId == null) {
        throw new ApiException(ClubExceptionType.FORBIDDEN_OPERATION, "partnerId is required");
      }
      authorizePartner(partnerId, actorId, roles);
    }
    LocalDate start = from == null ? LocalDate.now().minusMonths(1) : from;
    LocalDate end = to == null ? LocalDate.now().plusDays(BOOKING_WINDOW_DAYS) : to;
    List<Booking> bookings =
        bookingRepository
            .findByClassDateBetweenOrderByClassDateAscClassStartTimeAsc(start, end)
            .stream()
            .filter(b -> partnerId == null || b.getPartnerId().equals(partnerId))
            .toList();
    return toResponses(bookings, admin);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<UUID, Long> counts(List<UUID> classIds) {
    if (classIds == null || classIds.isEmpty()) return Map.of();
    return bookingRepository.findByClassIdInAndStatusIn(classIds, SEAT_STATUSES).stream()
        .collect(Collectors.groupingBy(Booking::getClassId, Collectors.counting()));
  }

  @Override
  @Transactional(readOnly = true)
  public BookingStats stats() {
    List<Booking> all = bookingRepository.findAll();
    List<Checkin> checkins = checkinRepository.findAll();
    return new BookingStats(
        all.size(),
        count(all, SessionBookingStatus.CONFIRMED),
        count(all, SessionBookingStatus.COMPLETED),
        count(all, SessionBookingStatus.NO_SHOW),
        count(all, SessionBookingStatus.CANCELLED),
        checkins.stream().filter(c -> c.getMethod() == CheckinMethod.QR_CODE).count(),
        checkins.stream().filter(c -> c.getMethod() == CheckinMethod.MANUAL_PARTNER).count());
  }

  // ---------- No-show ----------

  /** A confirmed booking whose check-in window has passed becomes a no-show (no partner payout). */
  @Override
  @Scheduled(fixedDelay = 300_000)
  public void markNoShows() {
    AppSettingsInfo settings;
    try {
      settings = adminClient.settings();
    } catch (RuntimeException ex) {
      log.warn("No-show job skipped: admin-service unavailable ({})", ex.getMessage());
      return;
    }
    LocalDateTime now = LocalDateTime.now();
    for (Booking b :
        bookingRepository.findByStatusAndClassDateLessThanEqual(
            SessionBookingStatus.CONFIRMED, now.toLocalDate())) {
      LocalDateTime windowEnd =
          LocalDateTime.of(b.getClassDate(), b.getClassStartTime())
              .plusMinutes(settings.checkinAfterMinutes());
      if (now.isAfter(windowEnd)) {
        b.setStatus(SessionBookingStatus.NO_SHOW);
        bookingRepository.save(b);
      }
    }
  }

  // ---------- Helpers ----------

  public static String partnerCode(UUID partnerId) {
    return partnerId.toString().substring(0, 8).toUpperCase();
  }

  /** Accepts a scanned "ONEPASS:XXXXXXXX" QR value or a raw code. */
  public static String readCode(String raw) {
    return raw.trim().toUpperCase().replaceFirst("^ONEPASS:", "").trim();
  }

  private Booking ownedBooking(UUID userId, UUID bookingId) {
    Booking booking =
        bookingRepository
            .findById(bookingId)
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.BOOKING_NOT_FOUND, "Booking not found: " + bookingId));
    if (!booking.getUserId().equals(userId)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "This booking belongs to another member");
    }
    return booking;
  }

  private static void requireConfirmed(Booking booking) {
    if (booking.getStatus() != SessionBookingStatus.CONFIRMED) {
      throw new ApiException(
          ClubExceptionType.BOOKING_NOT_CONFIRMED, "Booking is " + booking.getStatus());
    }
  }

  private void requireNotCheckedIn(Booking booking) {
    if (booking.getStatus() == SessionBookingStatus.COMPLETED) {
      throw new ApiException(
          ClubExceptionType.CHECKIN_ALREADY_DONE, "This booking has already been checked in");
    }
  }

  private void authorizePartner(UUID partnerId, UUID actorId, String roles) {
    if (Roles.isAdmin(roles)) return;
    PartnerInfo partner = partnerClient.partner(partnerId);
    if (!partner.ownerUserId().equals(actorId)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "This booking concerns another partner");
    }
  }

  private static long count(List<Booking> all, SessionBookingStatus status) {
    return all.stream().filter(b -> b.getStatus() == status).count();
  }

  private BookingResponse toResponse(Booking booking, Checkin checkin, boolean showCredits) {
    return BookingMapper.toResponse(booking, checkin, showCredits);
  }

  private List<BookingResponse> toResponses(List<Booking> bookings, boolean showCredits) {
    if (bookings.isEmpty()) return List.of();
    Map<UUID, Checkin> byBooking =
        checkinRepository.findByBookingIdIn(bookings.stream().map(Booking::getId).toList()).stream()
            .collect(Collectors.toMap(Checkin::getBookingId, Function.identity()));
    return bookings.stream()
        .map(b -> toResponse(b, byBooking.get(b.getId()), showCredits))
        .toList();
  }
}
