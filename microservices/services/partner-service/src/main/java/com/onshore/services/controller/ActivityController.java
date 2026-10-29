package com.onshore.services.controller;

import com.onshore.common_lib.security.Roles;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import com.onshore.services.service.ActivityService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

  private final ActivityService activityService;

  @GetMapping
  public List<ActivityResponse> list(
      @RequestParam(required = false) UUID partnerId,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Boolean active,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return activityService.list(partnerId, category, active, showPayout(roles));
  }

  @GetMapping("/{id}")
  public ActivityResponse get(
      @PathVariable UUID id,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return activityService.get(id, showPayout(roles));
  }

  @PostMapping
  public ResponseEntity<ActivityResponse> create(
      @Valid @RequestBody ActivityRequest request,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(activityService.create(request, userId, Roles.isAdmin(roles), showPayout(roles)));
  }

  @PutMapping("/{id}")
  public ActivityResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody ActivityRequest request,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return activityService.update(id, request, userId, Roles.isAdmin(roles), showPayout(roles));
  }

  private static boolean showPayout(String roles) {
    return Roles.isAdmin(roles) || Roles.has(roles, Roles.PARTNER);
  }
}
