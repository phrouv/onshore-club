package com.onshore.services.service;

import com.onshore.services.payload.CreditSettingDto;
import com.onshore.services.payload.PlanDto;
import java.util.List;

/** Admin-managed plans and credit grid. */
public interface CatalogService {

  List<PlanDto> plans(boolean includeInactive);

  PlanDto savePlan(PlanDto plan);

  List<CreditSettingDto> settings();

  CreditSettingDto saveSetting(String category, CreditSettingDto setting);
}
