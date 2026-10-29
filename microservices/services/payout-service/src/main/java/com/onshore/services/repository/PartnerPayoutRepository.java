package com.onshore.services.repository;

import com.onshore.services.model.PartnerPayout;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerPayoutRepository extends JpaRepository<PartnerPayout, UUID> {

  Optional<PartnerPayout> findByBookingId(UUID bookingId);

  List<PartnerPayout> findByPartnerId(UUID partnerId);
}
