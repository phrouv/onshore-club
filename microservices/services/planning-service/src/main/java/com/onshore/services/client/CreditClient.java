package com.onshore.services.client;

import com.onshore.common_lib.enums.DemandLevel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "credit-service")
public interface CreditClient {

  @GetMapping("/internal/credits/price")
  int price(@RequestParam("category") String category, @RequestParam("demand") DemandLevel demand);
}
