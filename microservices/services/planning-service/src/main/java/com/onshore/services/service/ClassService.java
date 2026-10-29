package com.onshore.services.service;

import com.onshore.common_lib.club.ClassInfo;
import com.onshore.common_lib.enums.ClassStatus;
import com.onshore.services.payload.ClassRequest;
import com.onshore.services.payload.ClassResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ClassService {

  List<ClassResponse> create(ClassRequest request, UUID userId, String roles);

  ClassResponse get(UUID id, String roles);

  List<ClassResponse> list(
      LocalDate from,
      LocalDate to,
      UUID activityId,
      UUID partnerId,
      ClassStatus status,
      String roles);

  ClassResponse update(UUID id, ClassRequest request, UUID userId, String roles);

  ClassResponse close(UUID id, UUID userId, String roles);

  ClassResponse reopen(UUID id, UUID userId, String roles);

  void delete(UUID id, UUID userId, String roles);

  ClassInfo info(UUID id);
}
