package com.onshore.services.repository;

import com.onshore.services.model.UsageRule;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRuleRepository extends JpaRepository<UsageRule, UUID> {

  List<UsageRule> findByActiveTrue();
}
