package com.onshore.services.client;

import com.onshore.common_lib.club.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payout-service")
public interface PayoutClient {

  @PostMapping("/internal/payouts")
  void create(@RequestBody PayoutCreateRequest request);
}
