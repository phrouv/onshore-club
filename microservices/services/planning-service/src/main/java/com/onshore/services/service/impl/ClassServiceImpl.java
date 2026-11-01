package com.onshore.services.service.impl;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.common_lib.club.ClassInfo;
import com.onshore.common_lib.club.PartnerInfo;
import com.onshore.common_lib.enums.ClassStatus;
import com.onshore.common_lib.enums.DemandLevel;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.client.BookingClient;
import com.onshore.services.client.CreditClient;
import com.onshore.services.client.PartnerClient;
import com.onshore.services.model.ClassSession;
import com.onshore.services.payload.ClassRequest;
import com.onshore.services.payload.ClassResponse;
import com.onshore.services.repository.ClassSessionRepository;
import com.onshore.services.service.ClassService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassServiceImpl implements ClassService {

  private static final int DEFAULT_WINDOW_DAYS = 8;

  private final ClassSessionRepository classRepository;
  private final PartnerClient partnerClient;
  private final BookingClient bookingClient;
  private final CreditClient creditClient;

  @Override
  public List<ClassResponse> create(ClassRequest request, UUID userId, String roles) {
    validateTimes(request);
    ActivityInfo activity = partnerClient.activity(request.activityId());
    authorize(activity.partnerId(), userId, roles);
    DemandLevel demand = demandFor(request, roles, DemandLevel.NORMAL);
    int credits = creditsFor(activity, demand);
    int weeks = request.recurrenceWeeks() == null ? 1 : request.recurrenceWeeks();

    List<ClassSession> created = new ArrayList<>();
    for (int i = 0; i < weeks; i++) {
      LocalDate date = request.date().plusWeeks(i);
      // Duplicates are silently skipped so a recurrence can be re-applied safely.
      if (classRepository.existsByActivityIdAndDateAndStartTime(
          activity.id(), date, request.startTime())) {
        continue;
      }
      created.add(
          classRepository.save(
              ClassSession.builder()
                  .activityId(activity.id())
                  .partnerId(activity.partnerId())
                  .date(date)
                  .startTime(request.startTime())
                  .endTime(request.endTime())
                  .capacity(request.capacity())
                  .onePassCapacity(request.onePassCapacity())
                  .demandLevel(demand)
                  .creditsRequired(credits)
                  .build()));
    }
    return toResponses(created, roles);
  }

  @Override
  @Transactional(readOnly = true)
  public ClassResponse get(UUID id, String roles) {
    return toResponses(List.of(find(id)), roles).getFirst();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClassResponse> list(
      LocalDate from,
      LocalDate to,
      UUID activityId,
      UUID partnerId,
      ClassStatus status,
      String roles) {
    LocalDate start = from == null ? LocalDate.now() : from;
    LocalDate end = to == null ? start.plusDays(DEFAULT_WINDOW_DAYS) : to;
    List<ClassSession> sessions =
        classRepository.findByDateBetweenOrderByDateAscStartTimeAsc(start, end).stream()
            .filter(c -> activityId == null || c.getActivityId().equals(activityId))
            .filter(c -> partnerId == null || c.getPartnerId().equals(partnerId))
            .filter(c -> status == null || c.getStatus() == status)
            .toList();
    return toResponses(sessions, roles);
  }

  @Override
  public ClassResponse update(UUID id, ClassRequest request, UUID userId, String roles) {
    validateTimes(request);
    ClassSession session = find(id);
    authorize(session.getPartnerId(), userId, roles);
    long booked = activeBookings(id);
    if (request.onePassCapacity() < booked) {
      throw new ApiException(
          ClubExceptionType.CLASS_HAS_BOOKINGS,
          "Cannot go down to "
              + request.onePassCapacity()
              + " One Pass seats: "
              + booked
              + " booking(s) already made");
    }
    session.setDate(request.date());
    session.setStartTime(request.startTime());
    session.setEndTime(request.endTime());
    session.setCapacity(request.capacity());
    session.setOnePassCapacity(request.onePassCapacity());
    if (UserRole.isAdmin(roles)
        && request.demandLevel() != null
        && request.demandLevel() != session.getDemandLevel()) {
      session.setDemandLevel(request.demandLevel());
      session.setCreditsRequired(
          creditsFor(partnerClient.activity(session.getActivityId()), request.demandLevel()));
    }
    return toResponses(List.of(classRepository.save(session)), roles).getFirst();
  }

  @Override
  public ClassResponse close(UUID id, UUID userId, String roles) {
    ClassSession session = find(id);
    authorize(session.getPartnerId(), userId, roles);
    long booked = activeBookings(id);
    if (booked > 0) {
      throw new ApiException(
          ClubExceptionType.CLASS_HAS_BOOKINGS,
          "This class has " + booked + " booking(s): it cannot be closed");
    }
    session.setStatus(ClassStatus.CLOSED);
    return toResponses(List.of(classRepository.save(session)), roles).getFirst();
  }

  @Override
  public ClassResponse reopen(UUID id, UUID userId, String roles) {
    ClassSession session = find(id);
    authorize(session.getPartnerId(), userId, roles);
    session.setStatus(ClassStatus.OPEN);
    return toResponses(List.of(classRepository.save(session)), roles).getFirst();
  }

  @Override
  public void delete(UUID id, UUID userId, String roles) {
    ClassSession session = find(id);
    authorize(session.getPartnerId(), userId, roles);
    if (activeBookings(id) > 0) {
      throw new ApiException(
          ClubExceptionType.CLASS_HAS_BOOKINGS, "This class has bookings: it cannot be deleted");
    }
    classRepository.delete(session);
  }

  @Override
  @Transactional(readOnly = true)
  public ClassInfo info(UUID id) {
    ClassSession c = find(id);
    return new ClassInfo(
        c.getId(),
        c.getActivityId(),
        c.getPartnerId(),
        c.getDate(),
        c.getStartTime(),
        c.getEndTime(),
        c.getOnePassCapacity(),
        c.getCreditsRequired(),
        c.getStatus());
  }

  private ClassSession find(UUID id) {
    return classRepository
        .findById(id)
        .orElseThrow(
            () -> new ApiException(ClubExceptionType.CLASS_NOT_FOUND, "Class not found: " + id));
  }

  private static void validateTimes(ClassRequest r) {
    if (!r.endTime().isAfter(r.startTime())) {
      throw new ApiException(ClubExceptionType.INVALID_PAYLOAD, "endTime must be after startTime");
    }
    if (r.onePassCapacity() > r.capacity()) {
      throw new ApiException(
          ClubExceptionType.INVALID_PAYLOAD, "onePassCapacity cannot exceed capacity");
    }
  }

  /** Partners can only manage classes of their own venue; admins can manage all. */
  private void authorize(UUID partnerId, UUID userId, String roles) {
    if (UserRole.isAdmin(roles)) return;
    PartnerInfo partner = partnerClient.partner(partnerId);
    if (!partner.ownerUserId().equals(userId)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "Not the owner of this partner");
    }
  }

  private static DemandLevel demandFor(ClassRequest request, String roles, DemandLevel fallback) {
    return UserRole.isAdmin(roles) && request.demandLevel() != null
        ? request.demandLevel()
        : fallback;
  }

  /** The activity's own price wins; otherwise the admin-managed grid by category and demand. */
  private int creditsFor(ActivityInfo activity, DemandLevel demand) {
    return activity.defaultCredits() != null
        ? activity.defaultCredits()
        : creditClient.price(activity.category(), demand);
  }

  private long activeBookings(UUID classId) {
    return bookingClient.counts(List.of(classId)).getOrDefault(classId, 0L);
  }

  private List<ClassResponse> toResponses(List<ClassSession> sessions, String roles) {
    if (sessions.isEmpty()) return List.of();
    Map<UUID, Long> counts =
        bookingClient.counts(sessions.stream().map(ClassSession::getId).toList());
    boolean admin = UserRole.isAdmin(roles);
    boolean partner = UserRole.has(roles, UserRole.ROLE_PARTNER.name());
    return sessions.stream()
        .map(
            c ->
                new ClassResponse(
                    c.getId(),
                    c.getActivityId(),
                    c.getPartnerId(),
                    c.getDate(),
                    c.getStartTime(),
                    c.getEndTime(),
                    c.getCapacity(),
                    c.getOnePassCapacity(),
                    Math.max(
                        0, c.getOnePassCapacity() - counts.getOrDefault(c.getId(), 0L).intValue()),
                    admin ? c.getDemandLevel() : null,
                    admin || !partner ? c.getCreditsRequired() : null,
                    c.getStatus()))
        .toList();
  }
}
