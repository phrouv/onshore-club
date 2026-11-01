package com.onshore.services.mapper;

import com.onshore.common_lib.model.Address;
import com.onshore.common_lib.model.ContactPerson;
import com.onshore.services.model.Partner;
import com.onshore.services.payload.PartnerRequest;
import com.onshore.services.payload.PartnerResponse;

public class PartnerMapper {

  private PartnerMapper() {}

  public static Partner toEntity(PartnerRequest request) {
    Partner entity = new Partner();
    entity.setName(request.name());
    entity.setAddress(address(request));
    entity.setDescription(request.description());
    entity.setWebsite(request.website());
    entity.setInstagram(request.instagram());
    entity.setManager(manager(request));
    return entity;
  }

  public static void update(Partner entity, PartnerRequest request) {
    if (request.name() != null) entity.setName(request.name());
    if (request.street() != null || request.city() != null || request.zipCode() != null) {
      Address address = entity.getAddress();
      if (address == null) {
        address = new Address();
        entity.setAddress(address);
      }
      if (request.street() != null) address.setStreet(request.street());
      if (request.city() != null) address.setCity(request.city());
      if (request.zipCode() != null) address.setZipCode(request.zipCode());
    }
    if (request.description() != null) entity.setDescription(request.description());
    if (request.website() != null) entity.setWebsite(request.website());
    if (request.instagram() != null) entity.setInstagram(request.instagram());
    if (request.managerFirstName() != null
        || request.managerLastName() != null
        || request.managerEmail() != null
        || request.managerPhoneNumber() != null) {
      ContactPerson manager = entity.getManager();
      if (manager == null) {
        manager = new ContactPerson();
        entity.setManager(manager);
      }
      if (request.managerFirstName() != null) manager.setFirstName(request.managerFirstName());
      if (request.managerLastName() != null) manager.setLastName(request.managerLastName());
      if (request.managerEmail() != null) manager.setEmail(request.managerEmail());
      if (request.managerPhoneNumber() != null)
        manager.setPhoneNumber(request.managerPhoneNumber());
    }
  }

  public static PartnerResponse toResponse(Partner e) {
    return new PartnerResponse(
        e.getId(),
        e.getName(),
        street(e),
        city(e),
        zipCode(e),
        e.getDescription(),
        e.getStatus(),
        e.getWebsite(),
        e.getInstagram(),
        firstName(e),
        lastName(e),
        email(e),
        phoneNumber(e),
        e.getOwnerUserId(),
        e.getCreatedDate(),
        e.getLastModifiedDate());
  }

  private static Address address(PartnerRequest request) {
    Address address = new Address();
    address.setStreet(request.street());
    address.setCity(request.city());
    address.setZipCode(request.zipCode());
    return address;
  }

  private static ContactPerson manager(PartnerRequest request) {
    ContactPerson manager = new ContactPerson();
    manager.setFirstName(request.managerFirstName());
    manager.setLastName(request.managerLastName());
    manager.setEmail(request.managerEmail());
    manager.setPhoneNumber(request.managerPhoneNumber());
    return manager;
  }

  private static String street(Partner e) {
    return e.getAddress() == null ? null : e.getAddress().getStreet();
  }

  private static String city(Partner e) {
    return e.getAddress() == null ? null : e.getAddress().getCity();
  }

  private static String zipCode(Partner e) {
    return e.getAddress() == null ? null : e.getAddress().getZipCode();
  }

  private static String firstName(Partner e) {
    return e.getManager() == null ? null : e.getManager().getFirstName();
  }

  private static String lastName(Partner e) {
    return e.getManager() == null ? null : e.getManager().getLastName();
  }

  private static String email(Partner e) {
    return e.getManager() == null ? null : e.getManager().getEmail();
  }

  private static String phoneNumber(Partner e) {
    return e.getManager() == null ? null : e.getManager().getPhoneNumber();
  }
}
