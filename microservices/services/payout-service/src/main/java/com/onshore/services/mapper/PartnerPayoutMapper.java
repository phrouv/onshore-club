package com.onshore.services.mapper;

import com.onshore.services.model.PartnerPayout;
import com.onshore.services.payload.PartnerPayoutResponse;

public class PartnerPayoutMapper {

  private PartnerPayoutMapper() {}

  public static PartnerPayoutResponse toResponse(PartnerPayout e) {
    return new PartnerPayoutResponse(
        e.getId(),
        e.getPartnerId(),
        e.getBookingId(),
        e.getAmount(),
        e.getStatus(),
        e.getPaymentDate(),
        e.getCreatedAt());
  }
}
