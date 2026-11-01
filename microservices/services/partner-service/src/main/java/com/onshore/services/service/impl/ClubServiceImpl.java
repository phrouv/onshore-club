package com.onshore.services.service.impl;

import com.onshore.common_lib.club.ClubInfo;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.mapper.ClubMapper;
import com.onshore.services.model.Club;
import com.onshore.services.model.Partner;
import com.onshore.services.payload.ClubRequest;
import com.onshore.services.payload.ClubResponse;
import com.onshore.services.repository.ClubRepository;
import com.onshore.services.repository.PartnerRepository;
import com.onshore.services.service.ClubService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClubServiceImpl implements ClubService {

  private final ClubRepository clubRepository;
  private final PartnerRepository partnerRepository;

  @Override
  public ClubResponse create(ClubRequest request) {
    Partner partner =
        partnerRepository
            .findById(request.partnerId())
            .orElseThrow(
                () ->
                    new ApiException(
                        ClubExceptionType.PARTNER_NOT_FOUND,
                        "Partner not found: " + request.partnerId()));
    if (partner.getStatus() != PartnerStatus.ACTIVE) {
      throw new ApiException(ClubExceptionType.PARTNER_NOT_ACTIVE, "Partner is not approved yet");
    }
    if (clubRepository.existsByNameAndPartnerId(request.name(), request.partnerId())) {
      throw new ApiException(
          ClubExceptionType.CLUB_ALREADY_EXISTS, "Club already exists: " + request.name());
    }
    Club club = ClubMapper.toEntity(request);
    return ClubMapper.toResponse(clubRepository.save(club));
  }

  @Override
  public ClubResponse update(UUID id, ClubRequest request) {
    Club club =
        clubRepository
            .findById(id)
            .orElseThrow(
                () -> new ApiException(ClubExceptionType.CLUB_NOT_FOUND, "Club not found: " + id));
    boolean nameChanged = !club.getName().equals(request.name());
    boolean partnerChanged = !club.getPartnerId().equals(request.partnerId());
    if ((nameChanged || partnerChanged)
        && clubRepository.existsByNameAndPartnerId(request.name(), request.partnerId())) {
      throw new ApiException(
          ClubExceptionType.CLUB_ALREADY_EXISTS, "Club already exists: " + request.name());
    }
    ClubMapper.update(club, request);
    return ClubMapper.toResponse(clubRepository.save(club));
  }

  @Override
  public ClubResponse changeOpen(UUID id, boolean open) {
    Club club =
        clubRepository
            .findById(id)
            .orElseThrow(
                () -> new ApiException(ClubExceptionType.CLUB_NOT_FOUND, "Club not found: " + id));
    club.setOpen(open);
    return ClubMapper.toResponse(clubRepository.save(club));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClubResponse> list(UUID partnerId) {
    return clubRepository.findAll().stream()
        .filter(c -> partnerId == null || c.getPartnerId().equals(partnerId))
        .map(ClubMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ClubResponse get(UUID id) {
    Club club =
        clubRepository
            .findById(id)
            .orElseThrow(
                () -> new ApiException(ClubExceptionType.CLUB_NOT_FOUND, "Club not found: " + id));
    return ClubMapper.toResponse(club);
  }

  @Override
  @Transactional(readOnly = true)
  public ClubInfo info(UUID id) {
    Club club =
        clubRepository
            .findById(id)
            .orElseThrow(
                () -> new ApiException(ClubExceptionType.CLUB_NOT_FOUND, "Club not found: " + id));
    return ClubMapper.toInfo(club);
  }
}
