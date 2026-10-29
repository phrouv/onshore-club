package com.onshore.services.service.impl;

import com.onshore.common_lib.club.PartnerInfo;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.mapper.PartnerMapper;
import com.onshore.services.model.Partner;
import com.onshore.services.payload.PartnerRequest;
import com.onshore.services.payload.PartnerResponse;
import com.onshore.services.repository.PartnerRepository;
import com.onshore.services.service.PartnerService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PartnerServiceImpl implements PartnerService {

  private final PartnerRepository partnerRepository;

  @Override
  public PartnerResponse create(PartnerRequest request, UUID ownerUserId) {
    if (partnerRepository.findByOwnerUserId(ownerUserId).isPresent()) {
      throw new ApiException(
          ClubExceptionType.PARTNER_ALREADY_EXISTS, "This user already owns a partner");
    }
    Partner partner = PartnerMapper.toEntity(request);
    partner.setStatus(PartnerStatus.PENDING);
    partner.setOwnerUserId(ownerUserId);
    return PartnerMapper.toResponse(partnerRepository.save(partner));
  }

  @Override
  @Transactional(readOnly = true)
  public PartnerResponse get(UUID id) {
    return PartnerMapper.toResponse(find(id));
  }

  @Override
  @Transactional(readOnly = true)
  public PartnerResponse mine(UUID ownerUserId) {
    return partnerRepository
        .findByOwnerUserId(ownerUserId)
        .map(PartnerMapper::toResponse)
        .orElseThrow(
            () ->
                new ApiException(
                    ClubExceptionType.PARTNER_NOT_FOUND, "No partner for user " + ownerUserId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PartnerResponse> list(PartnerStatus status, String city, String category) {
    return partnerRepository.findAll().stream()
        .filter(p -> status == null || p.getStatus() == status)
        .filter(p -> city == null || city.equalsIgnoreCase(p.getCity()))
        .filter(p -> category == null || category.equalsIgnoreCase(p.getCategory()))
        .map(PartnerMapper::toResponse)
        .toList();
  }

  @Override
  public PartnerResponse update(UUID id, PartnerRequest request, UUID userId, boolean admin) {
    Partner partner = find(id);
    if (!admin && !partner.getOwnerUserId().equals(userId)) {
      throw new ApiException(
          ClubExceptionType.FORBIDDEN_OPERATION, "Not the owner of this partner");
    }
    PartnerMapper.update(partner, request);
    return PartnerMapper.toResponse(partnerRepository.save(partner));
  }

  @Override
  public PartnerResponse changeStatus(UUID id, PartnerStatus status) {
    Partner partner = find(id);
    partner.setStatus(status);
    return PartnerMapper.toResponse(partnerRepository.save(partner));
  }

  @Override
  @Transactional(readOnly = true)
  public PartnerInfo info(UUID id) {
    Partner p = find(id);
    return new PartnerInfo(p.getId(), p.getName(), p.getOwnerUserId(), p.getStatus());
  }

  private Partner find(UUID id) {
    return partnerRepository
        .findById(id)
        .orElseThrow(
            () ->
                new ApiException(ClubExceptionType.PARTNER_NOT_FOUND, "Partner not found: " + id));
  }
}
