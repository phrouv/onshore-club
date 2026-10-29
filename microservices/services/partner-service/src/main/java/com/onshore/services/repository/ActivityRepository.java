package com.onshore.services.repository;

import com.onshore.services.model.Activity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

  List<Activity> findByPartnerId(UUID partnerId);
}
