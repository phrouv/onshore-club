package com.onshore.services.service.impl;

import com.onshore.common_lib.club.AppSettingsInfo;
import com.onshore.common_lib.club.UsageRuleInfo;
import com.onshore.common_lib.enums.UsageScope;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.client.BookingClient;
import com.onshore.services.client.CreditClient;
import com.onshore.services.client.PayoutClient;
import com.onshore.services.mapper.UsageRuleMapper;
import com.onshore.services.model.AppSettings;
import com.onshore.services.model.UsageRule;
import com.onshore.services.payload.DashboardResponse;
import com.onshore.services.payload.SettingsRequest;
import com.onshore.services.payload.UsageRuleRequest;
import com.onshore.services.payload.UsageRuleResponse;
import com.onshore.services.repository.AppSettingsRepository;
import com.onshore.services.repository.UsageRuleRepository;
import com.onshore.services.service.AdminService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

  private static final String GLOBAL = "global";

  private final UsageRuleRepository ruleRepository;
  private final AppSettingsRepository settingsRepository;
  private final BookingClient bookingClient;
  private final CreditClient creditClient;
  private final PayoutClient payoutClient;

  @Override
  @Transactional(readOnly = true)
  public List<UsageRuleResponse> rules() {
    return ruleRepository.findAll().stream().map(UsageRuleMapper::toResponse).toList();
  }

  @Override
  public UsageRuleResponse createRule(UsageRuleRequest request) {
    validate(request);
    return UsageRuleMapper.toResponse(ruleRepository.save(UsageRuleMapper.toEntity(request)));
  }

  @Override
  public UsageRuleResponse updateRule(UUID id, UsageRuleRequest request) {
    validate(request);
    UsageRule rule =
        ruleRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.USAGE_RULE_NOT_FOUND, "Usage rule not found: " + id));
    UsageRuleMapper.update(rule, request);
    // maxUses null means unlimited, so it must be clearable.
    rule.setMaxUses(request.maxUses());
    rule.setPartnerId(request.partnerId());
    rule.setActivityId(request.activityId());
    return UsageRuleMapper.toResponse(ruleRepository.save(rule));
  }

  @Override
  public void deleteRule(UUID id) {
    if (!ruleRepository.existsById(id)) {
      throw new ApiException(ClubExceptionType.USAGE_RULE_NOT_FOUND, "Usage rule not found: " + id);
    }
    ruleRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public AppSettingsInfo settings() {
    AppSettings s = current();
    return new AppSettingsInfo(
        s.getCheckinBeforeMinutes(), s.getCheckinAfterMinutes(), s.getCancellationHours());
  }

  @Override
  public AppSettingsInfo updateSettings(SettingsRequest request) {
    AppSettings s = current();
    s.setCheckinBeforeMinutes(request.checkinBeforeMinutes());
    s.setCheckinAfterMinutes(request.checkinAfterMinutes());
    s.setCancellationHours(request.cancellationHours());
    settingsRepository.save(s);
    return settings();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UsageRuleInfo> enforcedRules() {
    return ruleRepository.findByActiveTrue().stream()
        .filter(r -> r.getMaxUses() != null)
        .map(
            r ->
                new UsageRuleInfo(
                    r.getScope(), r.getPartnerId(), r.getActivityId(), r.getMaxUses()))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public DashboardResponse dashboard() {
    var bookings = bookingClient.stats();
    var credits = creditClient.stats();
    var payouts = payoutClient.stats();
    BigDecimal revenue = credits.monthlyRecurringRevenue().add(credits.purchaseRevenue());
    return new DashboardResponse(
        bookings, credits, payouts, revenue, revenue.subtract(payouts.totalAmount()));
  }

  private AppSettings current() {
    return settingsRepository
        .findById(GLOBAL)
        .orElseGet(() -> settingsRepository.save(new AppSettings()));
  }

  private static void validate(UsageRuleRequest r) {
    if (r.scope() == UsageScope.PARTNER_PER_MONTH && r.partnerId() == null) {
      throw new ApiException(
          ClubExceptionType.INVALID_PAYLOAD, "partnerId is required for PARTNER_PER_MONTH");
    }
    if (r.scope() == UsageScope.ACTIVITY_PER_MONTH && r.activityId() == null) {
      throw new ApiException(
          ClubExceptionType.INVALID_PAYLOAD, "activityId is required for ACTIVITY_PER_MONTH");
    }
  }
}
