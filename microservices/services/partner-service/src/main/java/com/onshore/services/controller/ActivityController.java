package com.onshore.services.controller;

import com.onshore.common_lib.enums.ActivityStatus;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import com.onshore.services.service.ActivityService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
  public Page<ActivityResponse> list(
      @RequestParam(required = false) UUID clubId,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdDate") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDirection,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    Sort.Direction direction =
        sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    return activityService.list(
        clubId, categoryId, status == null ? null : parseStatus(status), pageable);
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

  @PatchMapping("/{id}/status")
  public ActivityResponse changeStatus(
      @PathVariable UUID id,
      @RequestParam(required = false) String status,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return activityService.changeStatus(id, parseStatus(status));
  }

  private static ActivityStatus parseStatus(String status) {
    try {
      return ActivityStatus.valueOf(status);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new ApiException(ClubExceptionType.INVALID_PAYLOAD, "Invalid status: " + status);
    }
  }
}
