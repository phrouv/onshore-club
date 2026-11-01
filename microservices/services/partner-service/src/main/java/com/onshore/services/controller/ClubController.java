package com.onshore.services.controller;

import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.payload.ClubRequest;
import com.onshore.services.payload.ClubResponse;
import com.onshore.services.service.ClubService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

  private final ClubService clubService;

  @PostMapping
  public ResponseEntity<ClubResponse> create(
      @Valid @RequestBody ClubRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return ResponseEntity.status(HttpStatus.CREATED).body(clubService.create(request));
  }

  @PutMapping("/{clubId}")
  public ClubResponse update(
      @PathVariable UUID clubId,
      @Valid @RequestBody ClubRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return clubService.update(clubId, request);
  }

  @PatchMapping("/{clubId}/open")
  public ClubResponse changeOpen(
      @PathVariable UUID clubId,
      @RequestParam(required = false) String open,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return clubService.changeOpen(clubId, parseOpen(open));
  }

  @GetMapping
  public List<ClubResponse> list(
      @RequestParam(required = false) UUID partnerId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return clubService.list(partnerId);
  }

  @GetMapping("/{clubId}")
  public ClubResponse get(
      @PathVariable UUID clubId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return clubService.get(clubId);
  }

  private static boolean parseOpen(String open) {
    if ("true".equalsIgnoreCase(open)) {
      return true;
    }
    if ("false".equalsIgnoreCase(open)) {
      return false;
    }
    throw new ApiException(ClubExceptionType.INVALID_PAYLOAD, "open must be true or false");
  }
}
