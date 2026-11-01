package com.onshore.services.controller;

import com.onshore.common_lib.club.PayoutCreateRequest;
import com.onshore.common_lib.club.PayoutStats;
import com.onshore.common_lib.enums.PayoutStatus;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.services.payload.PartnerPayoutResponse;
import com.onshore.services.payload.PayoutSummary;
import com.onshore.services.service.PayoutService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PayoutController {

  private final PayoutService payoutService;

  @GetMapping("/api/payouts")
  public List<PartnerPayoutResponse> list(
      @RequestParam(required = false) UUID partnerId,
      @RequestParam(required = false) PayoutStatus status,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return payoutService.list(partnerId, status, userId, roles);
  }

  @GetMapping("/api/payouts/monthly")
  public List<PayoutSummary> monthly(
      @RequestParam UUID partnerId,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return payoutService.monthly(partnerId, userId, roles);
  }

  @GetMapping("/api/payouts/summary")
  public List<PayoutSummary> summary(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return payoutService.byPartner();
  }

  @PatchMapping("/api/payouts/{id}/status")
  public PartnerPayoutResponse changeStatus(
      @PathVariable UUID id,
      @RequestParam PayoutStatus status,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return payoutService.changeStatus(id, status);
  }

  // ----- Service-to-service; never routed by the api-gateway -----

  @PostMapping("/internal/payouts")
  public PartnerPayoutResponse create(@RequestBody PayoutCreateRequest request) {
    return payoutService.create(request);
  }

  @GetMapping("/internal/payouts/stats")
  public PayoutStats stats() {
    return payoutService.stats();
  }
}
