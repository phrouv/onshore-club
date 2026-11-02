package com.onshore.services.repository;

import com.onshore.services.model.Activity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

  boolean existsByClubIdAndName(UUID clubId, String name);
}
