package com.onshore.services.client;

import com.onshore.common_lib.club.CreditStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "credit-service")
public interface CreditClient {

  @GetMapping("/internal/credits/stats")
  CreditStats stats();
}
