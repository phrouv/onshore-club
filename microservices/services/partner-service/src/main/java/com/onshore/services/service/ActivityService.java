package com.onshore.services.service;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import java.util.List;
import java.util.UUID;

public interface ActivityService {

  ActivityResponse create(ActivityRequest request, UUID userId, boolean admin, boolean showPayout);

  ActivityResponse get(UUID id, boolean showPayout);

  List<ActivityResponse> list(UUID partnerId, String category, Boolean active, boolean showPayout);

  ActivityResponse update(
      UUID id, ActivityRequest request, UUID userId, boolean admin, boolean showPayout);

  ActivityInfo info(UUID id);
}
