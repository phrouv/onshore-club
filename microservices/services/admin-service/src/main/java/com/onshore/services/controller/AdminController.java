package com.onshore.services.controller;

import com.onshore.common_lib.club.AppSettingsInfo;
import com.onshore.common_lib.club.UsageRuleInfo;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.services.payload.DashboardResponse;
import com.onshore.services.payload.SettingsRequest;
import com.onshore.services.payload.UsageRuleRequest;
import com.onshore.services.payload.UsageRuleResponse;
import com.onshore.services.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/api/admin/dashboard")
  public DashboardResponse dashboard(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return adminService.dashboard();
  }

  @GetMapping("/api/admin/usage-rules")
  public List<UsageRuleResponse> rules(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return adminService.rules();
  }

  @PostMapping("/api/admin/usage-rules")
  public ResponseEntity<UsageRuleResponse> createRule(
      @Valid @RequestBody UsageRuleRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createRule(request));
  }

  @PutMapping("/api/admin/usage-rules/{id}")
  public UsageRuleResponse updateRule(
      @PathVariable UUID id,
      @Valid @RequestBody UsageRuleRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return adminService.updateRule(id, request);
  }

  @DeleteMapping("/api/admin/usage-rules/{id}")
  public ResponseEntity<Void> deleteRule(
      @PathVariable UUID id,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    adminService.deleteRule(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/api/admin/settings")
  public AppSettingsInfo settings(
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return adminService.settings();
  }

  @PutMapping("/api/admin/settings")
  public AppSettingsInfo updateSettings(
      @Valid @RequestBody SettingsRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return adminService.updateSettings(request);
  }

  // ----- Service-to-service; never routed by the api-gateway -----

  @GetMapping("/internal/settings")
  public AppSettingsInfo internalSettings() {
    return adminService.settings();
  }

  @GetMapping("/internal/usage-rules")
  public List<UsageRuleInfo> internalRules() {
    return adminService.enforcedRules();
  }
}
