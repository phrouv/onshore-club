package com.onshore.services.mapper;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.services.model.Activity;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;

public class ActivityMapper {

  private ActivityMapper() {}

  public static Activity toEntity(ActivityRequest r) {
    Activity a = new Activity();
    a.setPartnerId(r.partnerId());
    update(a, r);
    return a;
  }

  public static void update(Activity a, ActivityRequest r) {
    a.setName(r.name());
    a.setCategory(r.category());
    a.setDescription(r.description());
    if (r.durationMinutes() != null) a.setDurationMinutes(r.durationMinutes());
    if (r.partnerPayout() != null) a.setPartnerPayout(r.partnerPayout());
    a.setDefaultCredits(r.defaultCredits());
    if (r.active() != null) a.setActive(r.active());
  }

  public static ActivityResponse toResponse(Activity a, boolean showPayout) {
    return new ActivityResponse(
        a.getId(),
        a.getPartnerId(),
        a.getName(),
        a.getCategory(),
        a.getDescription(),
        a.getDurationMinutes(),
        showPayout ? a.getPartnerPayout() : null,
        a.getDefaultCredits(),
        Boolean.TRUE.equals(a.getActive()),
        a.getCreatedAt());
  }

  public static ActivityInfo toInfo(Activity a) {
    return new ActivityInfo(
        a.getId(),
        a.getPartnerId(),
        a.getName(),
        a.getCategory(),
        a.getPartnerPayout(),
        a.getDefaultCredits(),
        Boolean.TRUE.equals(a.getActive()));
  }
}
