package com.onshore.services.repository;

import com.onshore.services.model.Partner;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, UUID> {

  List<Partner> findByOwnerUserId(UUID ownerUserId);
}
