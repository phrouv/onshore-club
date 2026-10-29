package com.onshore.services.client;

import com.onshore.common_lib.club.*;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "planning-service")
public interface PlanningClient {

  @GetMapping("/internal/classes/{id}")
  ClassInfo classInfo(@PathVariable("id") UUID id);
}
