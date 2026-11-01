package com.onshore.services.mapper;

import com.onshore.common_lib.club.ClubInfo;
import com.onshore.common_lib.model.Address;
import com.onshore.common_lib.model.ContactPerson;
import com.onshore.common_lib.model.GeoLocation;
import com.onshore.services.model.Club;
import com.onshore.services.payload.ClubRequest;
import com.onshore.services.payload.ClubResponse;
import java.math.BigDecimal;

public class ClubMapper {

  private ClubMapper() {}

  public static Club toEntity(ClubRequest request) {
    Club entity = new Club();
    entity.setPartnerId(request.partnerId());
    entity.setName(request.name());
    entity.setDescription(request.description());
    entity.setAddress(address(request));
    entity.setGeoLocation(geoLocation(request));
    entity.setContact(contact(request));
    return entity;
  }

  public static void update(Club entity, ClubRequest request) {
    entity.setPartnerId(request.partnerId());
    entity.setName(request.name());
    if (request.description() != null) entity.setDescription(request.description());
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
    if (request.latitude() != null || request.longitude() != null) {
      GeoLocation geoLocation = entity.getGeoLocation();
      if (geoLocation == null) {
        geoLocation = new GeoLocation();
        entity.setGeoLocation(geoLocation);
      }
      if (request.latitude() != null) geoLocation.setLatitude(request.latitude());
      if (request.longitude() != null) geoLocation.setLongitude(request.longitude());
    }
    if (request.contactFirstName() != null
        || request.contactLastName() != null
        || request.contactEmail() != null
        || request.contactPhoneNumber() != null) {
      ContactPerson contact = entity.getContact();
      if (contact == null) {
        contact = new ContactPerson();
        entity.setContact(contact);
      }
      if (request.contactFirstName() != null) contact.setFirstName(request.contactFirstName());
      if (request.contactLastName() != null) contact.setLastName(request.contactLastName());
      if (request.contactEmail() != null) contact.setEmail(request.contactEmail());
      if (request.contactPhoneNumber() != null)
        contact.setPhoneNumber(request.contactPhoneNumber());
    }
  }

  public static ClubResponse toResponse(Club e) {
    return new ClubResponse(
        e.getId(),
        e.getPartnerId(),
        e.getName(),
        e.getDescription(),
        street(e),
        city(e),
        zipCode(e),
        latitude(e),
        longitude(e),
        firstName(e),
        lastName(e),
        email(e),
        phoneNumber(e),
        e.isOpen(),
        e.getCreatedDate());
  }

  public static ClubInfo toInfo(Club e) {
    return new ClubInfo(
        e.getId(), e.getPartnerId(), e.getName(), city(e), latitude(e), longitude(e), e.isOpen());
  }

  private static Address address(ClubRequest request) {
    Address address = new Address();
    address.setStreet(request.street());
    address.setCity(request.city());
    address.setZipCode(request.zipCode());
    return address;
  }

  private static GeoLocation geoLocation(ClubRequest request) {
    GeoLocation geoLocation = new GeoLocation();
    geoLocation.setLatitude(request.latitude());
    geoLocation.setLongitude(request.longitude());
    return geoLocation;
  }

  private static ContactPerson contact(ClubRequest request) {
    ContactPerson contact = new ContactPerson();
    contact.setFirstName(request.contactFirstName());
    contact.setLastName(request.contactLastName());
    contact.setEmail(request.contactEmail());
    contact.setPhoneNumber(request.contactPhoneNumber());
    return contact;
  }

  private static String street(Club e) {
    return e.getAddress() == null ? null : e.getAddress().getStreet();
  }

  private static String city(Club e) {
    return e.getAddress() == null ? null : e.getAddress().getCity();
  }

  private static String zipCode(Club e) {
    return e.getAddress() == null ? null : e.getAddress().getZipCode();
  }

  private static BigDecimal latitude(Club e) {
    return e.getGeoLocation() == null ? null : e.getGeoLocation().getLatitude();
  }

  private static BigDecimal longitude(Club e) {
    return e.getGeoLocation() == null ? null : e.getGeoLocation().getLongitude();
  }

  private static String firstName(Club e) {
    return e.getContact() == null ? null : e.getContact().getFirstName();
  }

  private static String lastName(Club e) {
    return e.getContact() == null ? null : e.getContact().getLastName();
  }

  private static String email(Club e) {
    return e.getContact() == null ? null : e.getContact().getEmail();
  }

  private static String phoneNumber(Club e) {
    return e.getContact() == null ? null : e.getContact().getPhoneNumber();
  }
}
