package com.onshore.services.client;

import com.onshore.common_lib.club.BookingStats;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "booking-service")
public interface BookingClient {

  @GetMapping("/internal/stats")
  BookingStats stats();
}
