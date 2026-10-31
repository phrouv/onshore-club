package com.onshore.services.service;

import com.onshore.common_lib.club.PartnerInfo;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.services.payload.PartnerRequest;
import com.onshore.services.payload.PartnerResponse;
import java.util.List;
import java.util.UUID;

public interface PartnerService {

  PartnerResponse create(PartnerRequest request, UUID ownerUserId);

  PartnerResponse get(UUID id);

  PartnerResponse mine(UUID ownerUserId);

  List<PartnerResponse> list(PartnerStatus status, String city);

  PartnerResponse update(UUID id, PartnerRequest request, UUID userId, boolean admin);

  PartnerResponse changeStatus(UUID id, PartnerStatus status);

  PartnerInfo info(UUID id);
}
