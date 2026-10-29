package com.onshore.services.repository;

import com.onshore.services.model.Checkin;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckinRepository extends JpaRepository<Checkin, UUID> {

  List<Checkin> findByBookingIdIn(Collection<UUID> bookingIds);
}
