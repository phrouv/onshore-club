package com.onshore.services.controller;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.common_lib.club.PartnerInfo;
import com.onshore.services.service.ActivityService;
import com.onshore.services.service.PartnerService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** Service-to-service endpoints; never routed by the api-gateway. */
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

  private final PartnerService partnerService;
  private final ActivityService activityService;

  @GetMapping("/partners/{id}")
  public PartnerInfo partner(@PathVariable UUID id) {
    return partnerService.info(id);
  }

  @GetMapping("/activities/{id}")
  public ActivityInfo activity(@PathVariable UUID id) {
    return activityService.info(id);
  }
}
