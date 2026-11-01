package com.onshore.services.repository;

import com.onshore.services.model.Club;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, UUID> {

  boolean existsByNameAndPartnerId(String name, UUID partnerId);
}
