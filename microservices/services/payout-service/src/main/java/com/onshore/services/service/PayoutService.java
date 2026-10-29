package com.onshore.services.service;

import com.onshore.common_lib.club.PayoutCreateRequest;
import com.onshore.common_lib.club.PayoutStats;
import com.onshore.common_lib.enums.PayoutStatus;
import com.onshore.services.payload.PartnerPayoutResponse;
import com.onshore.services.payload.PayoutSummary;
import java.util.List;
import java.util.UUID;

public interface PayoutService {

  /** Idempotent per booking: a second call returns the existing payout. */
  PartnerPayoutResponse create(PayoutCreateRequest request);

  List<PartnerPayoutResponse> list(UUID partnerId, PayoutStatus status, UUID userId, String roles);

  PartnerPayoutResponse changeStatus(UUID id, PayoutStatus status);

  List<PayoutSummary> monthly(UUID partnerId, UUID userId, String roles);

  List<PayoutSummary> byPartner();

  PayoutStats stats();
}
