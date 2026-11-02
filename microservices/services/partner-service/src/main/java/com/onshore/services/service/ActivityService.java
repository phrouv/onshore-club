package com.onshore.services.service;

import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import java.util.List;
import java.util.UUID;

public interface ActivityService {

  ActivityResponse create(ActivityRequest request);

  ActivityResponse get(UUID id);

  List<ActivityResponse> list();

  ActivityResponse update(UUID id, ActivityRequest request);
}
