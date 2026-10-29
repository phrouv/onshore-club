package com.onshore.services.repository;

import com.onshore.services.model.Partner;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, UUID> {

  Optional<Partner> findByOwnerUserId(UUID ownerUserId);
}
