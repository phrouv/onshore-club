package com.onshore.services.mapper;

import com.onshore.services.model.Partner;
import com.onshore.services.payload.PartnerRequest;
import com.onshore.services.payload.PartnerResponse;

public class PartnerMapper {

  private PartnerMapper() {}

  public static Partner toEntity(PartnerRequest request) {
    Partner entity = new Partner();
    entity.setName(request.name());
    entity.setAddress(request.address());
    entity.setCity(request.city());
    entity.setDescription(request.description());
    entity.setPhone(request.phone());
    entity.setProfessionalEmail(request.professionalEmail());
    entity.setWebsite(request.website());
    entity.setInstagram(request.instagram());
    entity.setActivityTypes(request.activityTypes());
    entity.setDesiredSeats(request.desiredSeats());
    entity.setManagerFirstName(request.managerFirstName());
    entity.setManagerLastName(request.managerLastName());
    return entity;
  }

  public static void update(Partner entity, PartnerRequest request) {
    if (request.name() != null) entity.setName(request.name());
    if (request.address() != null) entity.setAddress(request.address());
    if (request.city() != null) entity.setCity(request.city());
    if (request.description() != null) entity.setDescription(request.description());
    if (request.phone() != null) entity.setPhone(request.phone());
    if (request.professionalEmail() != null)
      entity.setProfessionalEmail(request.professionalEmail());
    if (request.website() != null) entity.setWebsite(request.website());
    if (request.instagram() != null) entity.setInstagram(request.instagram());
    if (request.activityTypes() != null) entity.setActivityTypes(request.activityTypes());
    if (request.desiredSeats() != null) entity.setDesiredSeats(request.desiredSeats());
    if (request.managerFirstName() != null) entity.setManagerFirstName(request.managerFirstName());
    if (request.managerLastName() != null) entity.setManagerLastName(request.managerLastName());
  }

  public static PartnerResponse toResponse(Partner e) {
    return new PartnerResponse(
        e.getId(),
        e.getName(),
        e.getAddress(),
        e.getCity(),
        e.getDescription(),
        e.getStatus(),
        e.getPhone(),
        e.getProfessionalEmail(),
        e.getWebsite(),
        e.getInstagram(),
        e.getActivityTypes(),
        e.getDesiredSeats(),
        e.getManagerFirstName(),
        e.getManagerLastName(),
        e.getOwnerUserId(),
        e.getCreatedDate(),
        e.getLastModifiedDate());
  }
}
