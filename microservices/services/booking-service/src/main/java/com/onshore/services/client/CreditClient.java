package com.onshore.services.client;

import com.onshore.common_lib.club.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "credit-service")
public interface CreditClient {

  @PostMapping("/internal/credits/apply")
  void apply(@RequestBody CreditOperation operation);
}
