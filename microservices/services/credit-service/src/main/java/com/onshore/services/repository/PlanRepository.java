package com.onshore.services.repository;

import com.onshore.services.model.Plan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, String> {

  List<Plan> findAllByOrderBySortOrderAsc();
}
