package com.onshore.services.controller;

import com.onshore.common_lib.enums.UserRole;
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

  @PostMapping
  public ResponseEntity<ActivityResponse> create(
      @Valid @RequestBody ActivityRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return ResponseEntity.status(HttpStatus.CREATED).body(activityService.create(request));
  }

  @GetMapping
  public List<ActivityResponse> list(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return activityService.list();
  }

  @GetMapping("/{id}")
  public ActivityResponse get(
      @PathVariable UUID id,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return activityService.get(id);
  }

  @PutMapping("/{id}")
  public ActivityResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody ActivityRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return activityService.update(id, request);
  }
}
