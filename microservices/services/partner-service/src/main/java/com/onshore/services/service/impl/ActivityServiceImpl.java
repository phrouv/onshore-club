package com.onshore.services.service.impl;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.mapper.ActivityMapper;
import com.onshore.services.model.Activity;
import com.onshore.services.model.Partner;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import com.onshore.services.repository.ActivityRepository;
import com.onshore.services.repository.PartnerRepository;
import com.onshore.services.service.ActivityService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityServiceImpl implements ActivityService {

  private final ActivityRepository activityRepository;
  private final PartnerRepository partnerRepository;

  @Override
  public ActivityResponse create(
      ActivityRequest request, UUID userId, boolean admin, boolean showPayout) {
    Partner partner = authorizedPartner(request.partnerId(), userId, admin);
    if (!admin && partner.getStatus() != PartnerStatus.ACTIVE) {
      throw new ApiException(ClubExceptionType.PARTNER_NOT_ACTIVE, "Partner is not approved yet");
    }
    Activity activity = ActivityMapper.toEntity(request);
    if (!admin) {
      activity.setDefaultCredits(null);
    }
    return ActivityMapper.toResponse(activityRepository.save(activity), showPayout);
  }

  @Override
  @Transactional(readOnly = true)
  public ActivityResponse get(UUID id, boolean showPayout) {
    return ActivityMapper.toResponse(find(id), showPayout);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ActivityResponse> list(
      UUID partnerId, String category, Boolean active, boolean showPayout) {
    List<Activity> all =
        partnerId == null
            ? activityRepository.findAll()
            : activityRepository.findByPartnerId(partnerId);
    return all.stream()
        .filter(a -> category == null || category.equalsIgnoreCase(a.getCategory()))
        .filter(a -> active == null || active.equals(a.getActive()))
        .map(a -> ActivityMapper.toResponse(a, showPayout))
        .toList();
  }

  @Override
  public ActivityResponse update(
      UUID id, ActivityRequest request, UUID userId, boolean admin, boolean showPayout) {
    Activity activity = find(id);
    authorizedPartner(activity.getPartnerId(), userId, admin);
    Integer adminCredits = activity.getDefaultCredits();
    ActivityMapper.update(activity, request);
    if (!admin) {
      activity.setDefaultCredits(adminCredits);
    }
    return ActivityMapper.toResponse(activityRepository.save(activity), showPayout);
  }

  @Override
  @Transactional(readOnly = true)
  public ActivityInfo info(UUID id) {
    return ActivityMapper.toInfo(find(id));
  }

  private Activity find(UUID id) {
    return activityRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ApiException(
                    ClubExceptionType.ACTIVITY_NOT_FOUND, "Activity not found: " + id));
  }

  private Partner authorizedPartner(UUID partnerId, UUID userId, boolean admin) {
    Partner partner =
        partnerRepository
            .findById(partnerId)
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.PARTNER_NOT_FOUND, "Partner not found: " + partnerId));
    if (!admin && !partner.getOwnerUserId().equals(userId)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "Not the owner of this partner");
    }
    return partner;
  }
}
