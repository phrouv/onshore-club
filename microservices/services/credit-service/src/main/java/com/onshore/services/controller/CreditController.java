package com.onshore.services.controller;

import com.onshore.common_lib.enums.CreditTransactionType;
import com.onshore.common_lib.security.Roles;
import com.onshore.services.payload.*;
import com.onshore.services.service.CatalogService;
import com.onshore.services.service.CreditService;
import com.onshore.services.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CreditController {

  private final CreditService creditService;
  private final SubscriptionService subscriptionService;
  private final CatalogService catalogService;

  // ----- Member: balance, ledger, purchase -----

  @GetMapping("/api/credits/balance")
  public BalanceResponse balance(@RequestHeader("X-User-Id") UUID userId) {
    return new BalanceResponse(creditService.balance(userId));
  }

  @GetMapping("/api/credits/transactions")
  public List<CreditTransactionResponse> transactions(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestParam(required = false) UUID memberId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    // Admins can read any member's ledger.
    UUID target = memberId != null && Roles.isAdmin(roles) ? memberId : userId;
    return creditService.transactions(target);
  }

  @PostMapping("/api/credits/purchase")
  public BalanceResponse purchase(
      @Valid @RequestBody PurchaseRequest request, @RequestHeader("X-User-Id") UUID userId) {
    return new BalanceResponse(creditService.purchase(userId, request.credits(), request.price()));
  }

  @PostMapping("/api/credits/adjust")
  public BalanceResponse adjust(
      @Valid @RequestBody AdjustRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    Roles.requireAdmin(roles);
    CreditTransactionType type =
        request.type() == CreditTransactionType.PROMOTIONAL_CREDIT
            ? CreditTransactionType.PROMOTIONAL_CREDIT
            : CreditTransactionType.ADMIN_ADJUSTMENT;
    return new BalanceResponse(
        creditService.apply(request.userId(), request.amount(), type, request.description(), null));
  }

  // ----- Subscription -----

  @GetMapping("/api/subscriptions/me")
  public SubscriptionResponse mySubscription(@RequestHeader("X-User-Id") UUID userId) {
    return subscriptionService.current(userId);
  }

  @PostMapping("/api/subscriptions/change-plan")
  public SubscriptionResponse changePlan(
      @Valid @RequestBody ChangePlanRequest request, @RequestHeader("X-User-Id") UUID userId) {
    return subscriptionService.changePlan(userId, request.planId());
  }

  @PostMapping("/api/subscriptions/cancel")
  public SubscriptionResponse cancel(@RequestHeader("X-User-Id") UUID userId) {
    return subscriptionService.cancel(userId);
  }

  // ----- Plans and credit grid (admin-managed) -----

  @GetMapping("/api/plans")
  public List<PlanDto> plans(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return catalogService.plans(Roles.isAdmin(roles));
  }

  @PutMapping("/api/plans")
  public PlanDto savePlan(
      @Valid @RequestBody PlanDto plan,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    Roles.requireAdmin(roles);
    return catalogService.savePlan(plan);
  }

  @GetMapping("/api/credit-settings")
  public List<CreditSettingDto> settings(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    // The grid is internal pricing: admin only.
    Roles.requireAdmin(roles);
    return catalogService.settings();
  }

  @PutMapping("/api/credit-settings/{category}")
  public CreditSettingDto saveSetting(
      @PathVariable String category,
      @Valid @RequestBody CreditSettingDto setting,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    Roles.requireAdmin(roles);
    return catalogService.saveSetting(category, setting);
  }
}
