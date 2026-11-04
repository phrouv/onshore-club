package com.onshore.services.mapper;

import com.onshore.common_lib.club.ActivityInfo;
import com.onshore.common_lib.model.ContactPerson;
import com.onshore.services.model.Activity;
import com.onshore.services.payload.ActivityRequest;
import com.onshore.services.payload.ActivityResponse;
import java.util.UUID;

public class ActivityMapper {

  private ActivityMapper() {}

  public static Activity toEntity(ActivityRequest r) {
    Activity a = new Activity();
    a.setClubId(r.clubId());
    a.setCategoryId(r.categoryId());
    a.setName(r.name());
    a.setDescription(r.description());
    a.setInstructor(contact(r));
    a.setSubscriptionDate(r.subscriptionDate());
    return a;
  }

  public static void update(Activity a, ActivityRequest r) {
    if (r.clubId() != null) a.setClubId(r.clubId());
    if (r.categoryId() != null) a.setCategoryId(r.categoryId());
    if (r.name() != null) a.setName(r.name());
    a.setDescription(r.description());
    a.setInstructor(contact(r));
  }

  public static ActivityResponse toResponse(Activity a) {
    ContactPerson instructor = a.getInstructor();
    return new ActivityResponse(
        a.getId(),
        a.getClubId(),
        a.getCategoryId(),
        a.getName(),
        a.getDescription(),
        instructor == null ? null : instructor.getFirstName(),
        instructor == null ? null : instructor.getLastName(),
        instructor == null ? null : instructor.getEmail(),
        instructor == null ? null : instructor.getPhoneNumber(),
        a.getStatus(),
        a.getSubscriptionDate(),
        a.getCreatedDate());
  }

  public static ActivityInfo toInfo(Activity a, UUID partnerId) {
    return new ActivityInfo(
        a.getId(), a.getClubId(), partnerId, a.getName(), a.getCategoryId(), a.getStatus());
  }

  private static ContactPerson contact(ActivityRequest r) {
    if (r.instructorFirstName() == null
        && r.instructorLastName() == null
        && r.instructorEmail() == null
        && r.instructorPhoneNumber() == null) {
      return null;
    }
    ContactPerson c = new ContactPerson();
    c.setFirstName(r.instructorFirstName());
    c.setLastName(r.instructorLastName());
    c.setEmail(r.instructorEmail());
    c.setPhoneNumber(r.instructorPhoneNumber());
    return c;
  }
}
