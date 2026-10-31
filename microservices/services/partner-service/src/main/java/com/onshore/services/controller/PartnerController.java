package com.onshore.services.controller;

import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.security.Roles;
import com.onshore.services.payload.PartnerRequest;
import com.onshore.services.payload.PartnerResponse;
import com.onshore.services.service.PartnerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

  private final PartnerService partnerService;

  @PostMapping
  public ResponseEntity<PartnerResponse> signup(
      @Valid @RequestBody PartnerRequest request, @RequestHeader("X-User-Id") UUID userId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.create(request, userId));
  }

  @GetMapping
  public List<PartnerResponse> list(
      @RequestParam(required = false) PartnerStatus status,
      @RequestParam(required = false) String city,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    // Only admins may browse non-approved partners.
    PartnerStatus effective = Roles.isAdmin(roles) ? status : PartnerStatus.ACTIVE;
    return partnerService.list(effective, city);
  }

  @GetMapping("/mine")
  public PartnerResponse mine(@RequestHeader("X-User-Id") UUID userId) {
    return partnerService.mine(userId);
  }

  @GetMapping("/{id}")
  public PartnerResponse get(@PathVariable UUID id) {
    return partnerService.get(id);
  }

  @PutMapping("/{id}")
  public PartnerResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody PartnerRequest request,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return partnerService.update(id, request, userId, Roles.isAdmin(roles));
  }

  @PatchMapping("/{id}/status")
  public PartnerResponse changeStatus(
      @PathVariable UUID id,
      @RequestParam PartnerStatus status,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    Roles.requireAdmin(roles);
    return partnerService.changeStatus(id, status);
  }
}
