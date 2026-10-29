package com.onshore.services.repository;

import com.onshore.services.model.ClassSession;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSessionRepository extends JpaRepository<ClassSession, UUID> {

  List<ClassSession> findByDateBetweenOrderByDateAscStartTimeAsc(LocalDate from, LocalDate to);

  boolean existsByActivityIdAndDateAndStartTime(
      UUID activityId, LocalDate date, java.time.LocalTime startTime);
}
