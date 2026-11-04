package com.onshore.services.repository;

import com.onshore.services.model.Activity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository
    extends JpaRepository<Activity, UUID>, JpaSpecificationExecutor<Activity> {

  boolean existsByClubIdAndName(UUID clubId, String name);

  boolean existsByClubIdAndNameAndIdNot(UUID clubId, String name, UUID id);
}
