package com.onshore.services.service;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.common_lib.enums.ActivityStatus;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityService {

  ActivityResponse create(ActivityRequest request);

  ActivityResponse get(UUID id);

  ActivityInfo info(UUID id);

  Page<ActivityResponse> list(
      UUID clubId, UUID categoryId, ActivityStatus status, Pageable pageable);

  ActivityResponse update(UUID id, ActivityRequest request);

  ActivityResponse changeStatus(UUID id, ActivityStatus status);
}
