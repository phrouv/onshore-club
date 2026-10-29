package com.onshore.services.repository;

import com.onshore.common_lib.enums.SessionBookingStatus;
import com.onshore.services.model.Booking;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

  long countByClassIdAndStatusIn(UUID classId, Collection<SessionBookingStatus> statuses);

  List<Booking> findByClassIdInAndStatusIn(
      Collection<UUID> classIds, Collection<SessionBookingStatus> statuses);

  boolean existsByUserIdAndClassIdAndStatus(UUID userId, UUID classId, SessionBookingStatus status);

  List<Booking> findByUserIdOrderByClassDateDescClassStartTimeDesc(UUID userId);

  List<Booking> findByUserIdAndStatusIn(UUID userId, Collection<SessionBookingStatus> statuses);

  List<Booking> findByClassDateBetweenOrderByClassDateAscClassStartTimeAsc(
      LocalDate from, LocalDate to);

  List<Booking> findByStatusAndClassDateLessThanEqual(SessionBookingStatus status, LocalDate date);

  /** The booking code shown to the member is the first 8 characters of the booking id. */
  @Query(
      "select b from Booking b where upper(substring(cast(b.id as string), 1, 8)) = upper(:code)")
  List<Booking> findByCode(String code);
}
