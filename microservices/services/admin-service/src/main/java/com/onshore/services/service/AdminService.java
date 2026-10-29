package com.onshore.services.service;

import com.onshore.common_lib.club.AppSettingsInfo;
import com.onshore.common_lib.club.UsageRuleInfo;
import com.onshore.services.payload.DashboardResponse;
import com.onshore.services.payload.SettingsRequest;
import com.onshore.services.payload.UsageRuleRequest;
import com.onshore.services.payload.UsageRuleResponse;
import java.util.List;
import java.util.UUID;

public interface AdminService {

  List<UsageRuleResponse> rules();

  UsageRuleResponse createRule(UsageRuleRequest request);

  UsageRuleResponse updateRule(UUID id, UsageRuleRequest request);

  void deleteRule(UUID id);

  AppSettingsInfo settings();

  AppSettingsInfo updateSettings(SettingsRequest request);

  /** Active rules with a cap (maxUses set); unlimited rules are not enforced. */
  List<UsageRuleInfo> enforcedRules();

  DashboardResponse dashboard();
}
