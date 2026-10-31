package com.onshore.services.mapper;

import com.onshore.services.model.UsageRule;
import com.onshore.services.payload.UsageRuleRequest;
import com.onshore.services.payload.UsageRuleResponse;

public class UsageRuleMapper {

  private UsageRuleMapper() {}

  public static UsageRule toEntity(UsageRuleRequest request) {
    UsageRule entity = new UsageRule();
    entity.setScope(request.scope());
    entity.setPartnerId(request.partnerId());
    entity.setActivityId(request.activityId());
    entity.setMaxUses(request.maxUses());
    if (request.active() != null) entity.setActive(request.active());
    return entity;
  }

  public static void update(UsageRule entity, UsageRuleRequest request) {
    if (request.scope() != null) entity.setScope(request.scope());
    if (request.partnerId() != null) entity.setPartnerId(request.partnerId());
    if (request.activityId() != null) entity.setActivityId(request.activityId());
    if (request.maxUses() != null) entity.setMaxUses(request.maxUses());
    if (request.active() != null) entity.setActive(request.active());
  }

  public static UsageRuleResponse toResponse(UsageRule e) {
    return new UsageRuleResponse(
        e.getId(),
        e.getScope(),
        e.getPartnerId(),
        e.getActivityId(),
        e.getMaxUses(),
        e.getActive(),
        e.getCreatedDate(),
        e.getLastModifiedDate());
  }
}
