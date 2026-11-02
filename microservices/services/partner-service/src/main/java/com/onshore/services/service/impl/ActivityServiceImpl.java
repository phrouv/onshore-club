package com.onshore.services.service.impl;

import com.onshore.common_lib.club.CategoryInfo;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.client.CategoryClient;
import com.onshore.services.mapper.ActivityMapper;
import com.onshore.services.model.Activity;
import com.onshore.services.model.Club;
import com.onshore.services.model.Partner;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import com.onshore.services.repository.ActivityRepository;
import com.onshore.services.repository.ClubRepository;
import com.onshore.services.repository.PartnerRepository;
import com.onshore.services.service.ActivityService;
import java.time.LocalDate;
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
  private final ClubRepository clubRepository;
  private final PartnerRepository partnerRepository;
  private final CategoryClient categoryClient;

  @Override
  public ActivityResponse create(ActivityRequest request) {
    Club club =
        clubRepository
            .findById(request.clubId())
            .orElseThrow(() ->
                new ApiException(
                    ClubExceptionType.CLUB_NOT_FOUND, "Club not found: " + request.clubId()));
    if (!club.isOpen()) {
      throw new ApiException(ClubExceptionType.CLUB_CLOSED, "Club is closed: " + club.getId());
    }
    Partner partner =
        partnerRepository
            .findById(club.getPartnerId())
            .orElseThrow(() ->
                new ApiException(
                    ClubExceptionType.PARTNER_NOT_FOUND,
                    "Partner not found: " + club.getPartnerId()));
    if (partner.getStatus() != PartnerStatus.ACTIVE) {
      throw new ApiException(
          ClubExceptionType.PARTNER_NOT_ACTIVE, "Partner is not active: " + partner.getId());
    }
    CategoryInfo category = categoryClient.category(request.categoryId());
    if (category == null || !category.active()) {
      throw new ApiException(
          ClubExceptionType.CATEGORY_NOT_FOUND,
          "Category not found or inactive: " + request.categoryId());
    }
    if (activityRepository.existsByClubIdAndName(request.clubId(), request.name())) {
      throw new ApiException(
          ClubExceptionType.ACTIVITY_ALREADY_EXISTS, "Activity already exists: " + request.name());
    }
    Activity activity = ActivityMapper.toEntity(request);
    if (activity.getSubscriptionDate() == null) {
      activity.setSubscriptionDate(LocalDate.now());
    }
    return ActivityMapper.toResponse(activityRepository.save(activity));
  }

  @Override
  @Transactional(readOnly = true)
  public ActivityResponse get(UUID id) {
    return ActivityMapper.toResponse(find(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ActivityResponse> list() {
    return activityRepository.findAll().stream().map(ActivityMapper::toResponse).toList();
  }

  @Override
  public ActivityResponse update(UUID id, ActivityRequest request) {
    Activity activity = find(id);
    ActivityMapper.update(activity, request);
    return ActivityMapper.toResponse(activityRepository.save(activity));
  }

  private Activity find(UUID id) {
    return activityRepository
        .findById(id)
        .orElseThrow(() ->
            new ApiException(ClubExceptionType.ACTIVITY_NOT_FOUND, "Activity not found: " + id));
  }
}
