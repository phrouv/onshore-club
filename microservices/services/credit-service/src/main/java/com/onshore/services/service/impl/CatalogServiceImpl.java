package com.onshore.services.service.impl;

import com.onshore.services.model.CreditSetting;
import com.onshore.services.model.Plan;
import com.onshore.services.payload.CreditSettingDto;
import com.onshore.services.payload.PlanDto;
import com.onshore.services.repository.CreditSettingRepository;
import com.onshore.services.repository.PlanRepository;
import com.onshore.services.service.CatalogService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CatalogServiceImpl implements CatalogService {

  private final PlanRepository planRepository;
  private final CreditSettingRepository settingRepository;

  @Override
  @Transactional(readOnly = true)
  public List<PlanDto> plans(boolean includeInactive) {
    return planRepository.findAllByOrderBySortOrderAsc().stream()
        .filter(p -> includeInactive || p.getActive())
        .map(CatalogServiceImpl::toDto)
        .toList();
  }

  @Override
  public PlanDto savePlan(PlanDto dto) {
    Plan plan =
        planRepository.findById(dto.id()).orElseGet(() -> Plan.builder().id(dto.id()).build());
    plan.setName(dto.name());
    plan.setMonthlyPrice(dto.monthlyPrice());
    plan.setCreditsPerMonth(dto.creditsPerMonth());
    if (dto.positioning() != null) plan.setPositioning(dto.positioning());
    if (dto.popular() != null) plan.setPopular(dto.popular());
    if (dto.active() != null) plan.setActive(dto.active());
    if (dto.sortOrder() != null) plan.setSortOrder(dto.sortOrder());
    plan.setUpdatedAt(Instant.now());
    return toDto(planRepository.save(plan));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CreditSettingDto> settings() {
    return settingRepository.findAll().stream()
        .map(
            s ->
                new CreditSettingDto(
                    s.getId(),
                    s.getCreditsLow(),
                    s.getCreditsNormal(),
                    s.getCreditsHigh(),
                    s.getDefaultPayout()))
        .toList();
  }

  @Override
  public CreditSettingDto saveSetting(String category, CreditSettingDto dto) {
    CreditSetting s =
        settingRepository
            .findById(category)
            .orElseGet(() -> CreditSetting.builder().id(category).build());
    s.setCreditsLow(dto.creditsLow());
    s.setCreditsNormal(dto.creditsNormal());
    s.setCreditsHigh(dto.creditsHigh());
    s.setDefaultPayout(dto.defaultPayout());
    s.setUpdatedAt(Instant.now());
    settingRepository.save(s);
    return new CreditSettingDto(
        category,
        s.getCreditsLow(),
        s.getCreditsNormal(),
        s.getCreditsHigh(),
        s.getDefaultPayout());
  }

  private static PlanDto toDto(Plan p) {
    return new PlanDto(
        p.getId(),
        p.getName(),
        p.getMonthlyPrice(),
        p.getCreditsPerMonth(),
        p.getPositioning(),
        p.getPopular(),
        p.getActive(),
        p.getSortOrder());
  }
}
