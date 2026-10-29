package com.onshore.services.controller;

import com.onshore.common_lib.club.CreditOperation;
import com.onshore.common_lib.club.CreditStats;
import com.onshore.common_lib.enums.DemandLevel;
import com.onshore.services.payload.BalanceResponse;
import com.onshore.services.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** Service-to-service endpoints; never routed by the api-gateway. */
@RestController
@RequestMapping("/internal/credits")
@RequiredArgsConstructor
public class InternalCreditController {

  private final CreditService creditService;

  /** The operation amount is signed: negative debits, positive (or zero) credits. */
  @PostMapping("/apply")
  public BalanceResponse apply(@RequestBody CreditOperation op) {
    return new BalanceResponse(
        creditService.apply(op.userId(), op.amount(), op.type(), op.description(), null));
  }

  @GetMapping("/price")
  public int price(@RequestParam String category, @RequestParam DemandLevel demand) {
    return creditService.price(category, demand);
  }

  @GetMapping("/stats")
  public CreditStats stats() {
    return creditService.stats();
  }
}
