package com.onshore.services.client;

import com.onshore.common_lib.club.*;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "admin-service")
public interface AdminClient {

  @GetMapping("/internal/settings")
  AppSettingsInfo settings();

  @GetMapping("/internal/usage-rules")
  List<UsageRuleInfo> usageRules();
}
