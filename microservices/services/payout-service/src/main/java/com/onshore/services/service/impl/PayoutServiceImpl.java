package com.onshore.services.service.impl;

import com.onshore.common_lib.club.PartnerInfo;
import com.onshore.common_lib.club.PayoutCreateRequest;
import com.onshore.common_lib.club.PayoutStats;
import com.onshore.common_lib.enums.PayoutStatus;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.client.PartnerClient;
import com.onshore.services.mapper.PartnerPayoutMapper;
import com.onshore.services.model.PartnerPayout;
import com.onshore.services.payload.PartnerPayoutResponse;
import com.onshore.services.payload.PayoutSummary;
import com.onshore.services.repository.PartnerPayoutRepository;
import com.onshore.services.service.PayoutService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PayoutServiceImpl implements PayoutService {

  private final PartnerPayoutRepository payoutRepository;
  private final PartnerClient partnerClient;

  @Override
  public PartnerPayoutResponse create(PayoutCreateRequest request) {
    return payoutRepository
        .findByBookingId(request.bookingId())
        .map(PartnerPayoutMapper::toResponse)
        .orElseGet(
            () ->
                PartnerPayoutMapper.toResponse(
                    payoutRepository.save(
                        PartnerPayout.builder()
                            .partnerId(request.partnerId())
                            .bookingId(request.bookingId())
                            .amount(request.amount())
                            .build())));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PartnerPayoutResponse> list(
      UUID partnerId, PayoutStatus status, UUID userId, String roles) {
    authorize(partnerId, userId, roles);
    List<PartnerPayout> source =
        partnerId == null
            ? payoutRepository.findAll()
            : payoutRepository.findByPartnerId(partnerId);
    return source.stream()
        .filter(p -> status == null || p.getStatus() == status)
        .sorted(Comparator.comparing(PartnerPayout::getCreatedAt).reversed())
        .map(PartnerPayoutMapper::toResponse)
        .toList();
  }

  @Override
  public PartnerPayoutResponse changeStatus(UUID id, PayoutStatus status) {
    PartnerPayout payout =
        payoutRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.PAYOUT_NOT_FOUND, "Payout not found: " + id));
    payout.setStatus(status);
    payout.setPaymentDate(status == PayoutStatus.PAID ? LocalDate.now() : null);
    return PartnerPayoutMapper.toResponse(payoutRepository.save(payout));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PayoutSummary> monthly(UUID partnerId, UUID userId, String roles) {
    authorize(partnerId, userId, roles);
    Map<String, List<PartnerPayout>> byMonth =
        payoutRepository.findByPartnerId(partnerId).stream()
            .collect(
                Collectors.groupingBy(
                    p ->
                        p.getCreatedAt()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                            .substring(0, 7)));
    return byMonth.entrySet().stream()
        .sorted(Map.Entry.<String, List<PartnerPayout>>comparingByKey().reversed())
        .map(e -> summarize(partnerId, e.getKey(), e.getValue()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<PayoutSummary> byPartner() {
    return payoutRepository.findAll().stream()
        .collect(Collectors.groupingBy(PartnerPayout::getPartnerId))
        .entrySet()
        .stream()
        .map(e -> summarize(e.getKey(), null, e.getValue()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PayoutStats stats() {
    List<PartnerPayout> all = payoutRepository.findAll();
    return new PayoutStats(
        all.size(), sum(all, null), sum(all, PayoutStatus.PENDING), sum(all, PayoutStatus.PAID));
  }

  private static PayoutSummary summarize(
      UUID partnerId, String month, List<PartnerPayout> payouts) {
    return new PayoutSummary(
        partnerId,
        month,
        payouts.size(),
        sum(payouts, PayoutStatus.PENDING),
        sum(payouts, PayoutStatus.PAID),
        sum(payouts, null));
  }

  private static BigDecimal sum(List<PartnerPayout> payouts, PayoutStatus status) {
    return payouts.stream()
        .filter(p -> status == null || p.getStatus() == status)
        .map(PartnerPayout::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /** Partners only see their own venue; admins see everything (partnerId may then be null). */
  private void authorize(UUID partnerId, UUID userId, String roles) {
    if (UserRole.isAdmin(roles)) return;
    if (partnerId == null) {
      throw new ApiException(ClubExceptionType.FORBIDDEN_OPERATION, "partnerId is required");
    }
    PartnerInfo partner = partnerClient.partner(partnerId);
    if (!partner.ownerUserId().equals(userId)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "Not the owner of this partner");
    }
  }
}
