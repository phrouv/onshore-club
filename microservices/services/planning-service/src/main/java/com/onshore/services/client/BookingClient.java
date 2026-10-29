package com.onshore.services.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service")
public interface BookingClient {

  @GetMapping("/internal/bookings/counts")
  Map<UUID, Long> counts(@RequestParam("classIds") List<UUID> classIds);
}
