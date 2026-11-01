package com.onshore.services.service;

import com.onshore.common_lib.club.ClubInfo;
import com.onshore.services.payload.ClubRequest;
import com.onshore.services.payload.ClubResponse;
import java.util.List;
import java.util.UUID;

public interface ClubService {

  ClubResponse create(ClubRequest request);

  ClubResponse update(UUID id, ClubRequest request);

  ClubResponse changeOpen(UUID id, boolean open);

  List<ClubResponse> list(UUID partnerId);

  ClubResponse get(UUID id);

  ClubInfo info(UUID id);
}
