package com.onshore.services.client;

import com.onshore.common_lib.club.PayoutStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payout-service")
public interface PayoutClient {

  @GetMapping("/internal/payouts/stats")
  PayoutStats stats();
}
