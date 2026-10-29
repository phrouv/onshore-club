package com.onshore.services.client;

import com.onshore.common_lib.club.*;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "partner-service")
public interface PartnerClient {

  @GetMapping("/internal/activities/{id}")
  ActivityInfo activity(@PathVariable("id") UUID id);

  @GetMapping("/internal/partners/{id}")
  PartnerInfo partner(@PathVariable("id") UUID id);
}
