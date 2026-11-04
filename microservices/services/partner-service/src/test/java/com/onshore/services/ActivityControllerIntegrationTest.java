package com.onshore.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onshore.common_lib.club.CategoryInfo;
import com.onshore.common_lib.enums.ActivityStatus;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.model.Address;
import com.onshore.common_lib.model.ContactPerson;
import com.onshore.services.client.CategoryClient;
import com.onshore.services.model.Activity;
import com.onshore.services.model.Club;
import com.onshore.services.model.Partner;
import com.onshore.services.repository.ActivityRepository;
import com.onshore.services.repository.ClubRepository;
import com.onshore.services.repository.PartnerRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** US001 - Create an Activity (AC1 to AC11); US002 - Update an Activity Profile (AC1 to AC8). */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class ActivityControllerIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  @Autowired private MockMvc mockMvc;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ClubRepository clubRepository;
  @Autowired private PartnerRepository partnerRepository;
  @MockitoBean private CategoryClient categoryClient;

  @BeforeEach
  void cleanUp() {
    activityRepository.deleteAll();
    clubRepository.deleteAll();
    partnerRepository.deleteAll();
    reset(categoryClient);
  }

  private static String body(UUID clubId, UUID categoryId, String name) {
    return "{\"clubId\":\"%s\",\"categoryId\":\"%s\",\"name\":\"%s\"}"
        .formatted(clubId, categoryId, name);
  }

  private MockHttpServletRequestBuilder as(String role, MockHttpServletRequestBuilder request) {
    return request.header("X-User-Roles", role).contentType(MediaType.APPLICATION_JSON);
  }

  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return as(UserRole.ROLE_SYSTEM_ADMIN.name(), request);
  }

  private Partner createPartner(PartnerStatus status) {
    Address address = new Address();
    address.setStreet("Main Street 1");
    address.setCity("Paris");
    address.setZipCode("75001");
    Partner partner = new Partner();
    partner.setName("Zen Studio " + UUID.randomUUID());
    partner.setOwnerUserId(UUID.randomUUID());
    partner.setStatus(status);
    partner.setAddress(address);
    return partnerRepository.save(partner);
  }

  private Club createClub(Partner partner, boolean open) {
    Address address = new Address();
    address.setStreet("Main Street 1");
    address.setCity("Paris");
    address.setZipCode("75001");
    Club club = new Club();
    club.setPartnerId(partner.getId());
    club.setName("Marais " + UUID.randomUUID());
    club.setAddress(address);
    club.setOpen(open);
    return clubRepository.save(club);
  }

  private void stubCategory(boolean active) {
    when(categoryClient.category(any(UUID.class)))
        .thenReturn(new CategoryInfo(UUID.randomUUID(), "YOGA", "Yoga", null, null, active));
  }

  private Activity createActivity(Club club, UUID categoryId, String name) {
    Activity activity = new Activity();
    activity.setClubId(club.getId());
    activity.setCategoryId(categoryId);
    activity.setName(name);
    activity.setSubscriptionDate(LocalDate.of(2026, 11, 1));
    return activityRepository.save(activity);
  }

  @Test
  @DisplayName("AC1: admin creates an activity for an open club with ACTIVE partner")
  void adminCreatesActivity() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities")).content(body(club.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.clubId").value(club.getId().toString()))
        .andExpect(jsonPath("$.name").value("Morning Yoga"))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.subscriptionDate").exists())
        .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  @DisplayName("AC2: create for a closed club is rejected with 409 CLUB_CLOSED")
  void closedClubIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, false);
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("CLUB_CLOSED"));
  }

  @Test
  @DisplayName(
      "AC3: create for a club whose partner is not ACTIVE is rejected with 409 PARTNER_NOT_ACTIVE")
  void inactivePartnerIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.PENDING);
    Club club = createClub(partner, true);
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("PARTNER_NOT_ACTIVE"));
  }

  @Test
  @DisplayName("AC4: unknown clubId is rejected with 404 CLUB_NOT_FOUND")
  void unknownClubIsRejected() throws Exception {
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(body(UUID.randomUUID(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CLUB_NOT_FOUND"));
  }

  @Test
  @DisplayName("AC5: inactive category is rejected with 404 CATEGORY_NOT_FOUND")
  void inactiveCategoryIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    stubCategory(false);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("AC5: unknown category is rejected with 404 CATEGORY_NOT_FOUND")
  void unknownCategoryIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    when(categoryClient.category(any(UUID.class))).thenReturn(null);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName(
      "AC6: duplicate name within the same club is rejected with 409 ACTIVITY_ALREADY_EXISTS")
  void duplicateNameInSameClubIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities")).content(body(club.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            admin(post("/api/activities")).content(body(club.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("ACTIVITY_ALREADY_EXISTS"));
  }

  @Test
  @DisplayName("AC7: missing mandatory field is rejected with 400 INVALID_PAYLOAD")
  void missingMandatoryFieldIsRejected() throws Exception {
    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(
                    "{\"clubId\":\"%s\",\"name\":\"Morning Yoga\"}".formatted(UUID.randomUUID())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("AC8: malformed instructorEmail is rejected with 400 INVALID_PAYLOAD")
  void malformedEmailIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(
                    "{\"clubId\":\"%s\",\"categoryId\":\"%s\",\"name\":\"Morning Yoga\",\"instructorEmail\":\"not-an-email\"}"
                        .formatted(club.getId(), UUID.randomUUID())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("AC9: optional fields omitted still creates the activity without them")
  void optionalFieldsOmittedIsCreated() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities"))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Morning Yoga"));

    assertThat(activityRepository.findAll().getFirst().getInstructor()).isNull();
    assertThat(activityRepository.findAll().getFirst().getDescription()).isNull();
  }

  @Test
  @DisplayName("AC10: same name in a different club is allowed")
  void duplicateNameInDifferentClubIsAllowed() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club1 = createClub(partner, true);
    Club club2 = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    stubCategory(true);

    mockMvc
        .perform(
            admin(post("/api/activities")).content(body(club1.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            admin(post("/api/activities")).content(body(club2.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("AC11: non-admin create is rejected with 403 FORBIDDEN_OPERATION")
  void nonAdminIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), post("/api/activities"))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("AC1: admin updates an activity profile and status/subscriptionDate are unchanged")
  void adminUpdatesActivity() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(
                    "{\"clubId\":\"%s\",\"categoryId\":\"%s\",\"name\":\"Evening Yoga\",\"description\":\"Updated\",\"instructorFirstName\":\"Alex\"}"
                        .formatted(club.getId(), categoryId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Evening Yoga"))
        .andExpect(jsonPath("$.description").value("Updated"))
        .andExpect(jsonPath("$.instructorFirstName").value("Alex"))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.subscriptionDate").value("2026-11-01"));
  }

  @Test
  @DisplayName("AC2: status and subscriptionDate in the payload are ignored")
  void statusAndSubscriptionDateAreIgnored() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(
                    "{\"clubId\":\"%s\",\"categoryId\":\"%s\",\"name\":\"Morning Yoga\",\"status\":\"REJECTED\",\"subscriptionDate\":\"2027-01-01\"}"
                        .formatted(club.getId(), categoryId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.subscriptionDate").value("2026-11-01"));
  }

  @Test
  @DisplayName("AC3: malformed instructorEmail is rejected with 400 INVALID_PAYLOAD")
  void updateMalformedEmailIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(
                    "{\"clubId\":\"%s\",\"categoryId\":\"%s\",\"name\":\"Morning Yoga\",\"instructorEmail\":\"not-an-email\"}"
                        .formatted(club.getId(), categoryId)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("AC4: unknown or inactive category is rejected with 404 CATEGORY_NOT_FOUND")
  void updateUnknownCategoryIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    stubCategory(false);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(body(club.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("AC5: name used by another activity of the same club is rejected with 409")
  void updateDuplicateNameInSameClubIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    createActivity(club, categoryId, "Evening Yoga");
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(body(club.getId(), categoryId, "Evening Yoga")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("ACTIVITY_ALREADY_EXISTS"));
  }

  @Test
  @DisplayName("AC5: keeping its own name is allowed")
  void keepingOwnNameIsAllowed() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(body(club.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Morning Yoga"));
  }

  @Test
  @DisplayName("AC6: moving to another club of the same partner is allowed")
  void moveToSamePartnerClubIsAllowed() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club1 = createClub(partner, true);
    Club club2 = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club1, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(body(club2.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clubId").value(club2.getId().toString()));
  }

  @Test
  @DisplayName("AC7: moving to a club of a different partner is rejected with 403")
  void moveToDifferentPartnerClubIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Partner other = createPartner(PartnerStatus.ACTIVE);
    Club club1 = createClub(partner, true);
    Club club2 = createClub(other, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club1, categoryId, "Morning Yoga");
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", activity.getId()))
                .content(body(club2.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("AC8: non-admin update is rejected with 403 FORBIDDEN_OPERATION")
  void nonAdminUpdateIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), put("/api/activities/{id}", activity.getId()))
                .content(body(club.getId(), categoryId, "Morning Yoga")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US002: updating an unknown activity id returns 404 ACTIVITY_NOT_FOUND")
  void updateUnknownActivityIsNotFound() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    stubCategory(true);

    mockMvc
        .perform(
            admin(put("/api/activities/{id}", UUID.randomUUID()))
                .content(body(club.getId(), UUID.randomUUID(), "Morning Yoga")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("ACTIVITY_NOT_FOUND"));
  }

  @Test
  @DisplayName("US003 AC1: admin suspends an ACTIVE activity")
  void adminSuspendsActivity() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    Activity activity = createActivity(club, UUID.randomUUID(), "Morning Yoga");

    mockMvc
        .perform(
            admin(patch("/api/activities/{id}/status", activity.getId()))
                .param("status", "SUSPENDED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SUSPENDED"));
  }

  @Test
  @DisplayName("US003 AC2: admin reactivates a SUSPENDED activity and its data is intact")
  void adminReactivatesActivity() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    activity.setStatus(ActivityStatus.SUSPENDED);
    activityRepository.save(activity);

    mockMvc
        .perform(
            admin(patch("/api/activities/{id}/status", activity.getId())).param("status", "ACTIVE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.name").value("Morning Yoga"))
        .andExpect(jsonPath("$.clubId").value(club.getId().toString()));
  }

  @Test
  @DisplayName("US003 AC3: admin rejects an activity")
  void adminRejectsActivity() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    Activity activity = createActivity(club, UUID.randomUUID(), "Morning Yoga");

    mockMvc
        .perform(
            admin(patch("/api/activities/{id}/status", activity.getId()))
                .param("status", "REJECTED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("REJECTED"));
  }

  @Test
  @DisplayName("US003 AC4: admin moves a REJECTED activity to SUSPENDED")
  void adminRejectedToSuspended() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    Activity activity = createActivity(club, UUID.randomUUID(), "Morning Yoga");
    activity.setStatus(ActivityStatus.REJECTED);
    activityRepository.save(activity);

    mockMvc
        .perform(
            admin(patch("/api/activities/{id}/status", activity.getId()))
                .param("status", "SUSPENDED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SUSPENDED"));
  }

  @Test
  @DisplayName("US003 AC5: invalid status value is rejected with 400 INVALID_PAYLOAD")
  void invalidStatusIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    Activity activity = createActivity(club, UUID.randomUUID(), "Morning Yoga");

    mockMvc
        .perform(
            admin(patch("/api/activities/{id}/status", activity.getId())).param("status", "FOO"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("US003 AC6: non-admin status change is rejected with 403 FORBIDDEN_OPERATION")
  void nonAdminStatusChangeIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    Activity activity = createActivity(club, UUID.randomUUID(), "Morning Yoga");

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), patch("/api/activities/{id}/status", activity.getId()))
                .param("status", "SUSPENDED"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US003: status change on unknown activity id returns 404 ACTIVITY_NOT_FOUND")
  void statusChangeUnknownActivityIsNotFound() throws Exception {
    mockMvc
        .perform(
            admin(patch("/api/activities/{id}/status", UUID.randomUUID()))
                .param("status", "SUSPENDED"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("ACTIVITY_NOT_FOUND"));
  }

  @Test
  @DisplayName("US004 AC1: filtering by status returns only matching activities")
  void listFiltersByStatus() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    createActivity(club, UUID.randomUUID(), "Active Yoga");
    Activity rejected = createActivity(club, UUID.randomUUID(), "Rejected Yoga");
    rejected.setStatus(ActivityStatus.REJECTED);
    activityRepository.save(rejected);

    mockMvc
        .perform(admin(get("/api/activities")).param("status", "REJECTED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Rejected Yoga"));
  }

  @Test
  @DisplayName("US004 AC2: filtering by clubId returns only that club's activities")
  void listFiltersByClub() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club1 = createClub(partner, true);
    Club club2 = createClub(partner, true);
    createActivity(club1, UUID.randomUUID(), "Yoga A");
    createActivity(club2, UUID.randomUUID(), "Yoga B");

    mockMvc
        .perform(admin(get("/api/activities")).param("clubId", club1.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].clubId").value(club1.getId().toString()));
  }

  @Test
  @DisplayName("US004 AC3: filtering by categoryId returns only that category's activities")
  void listFiltersByCategory() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryA = UUID.randomUUID();
    UUID categoryB = UUID.randomUUID();
    createActivity(club, categoryA, "Yoga A");
    createActivity(club, categoryB, "Yoga B");

    mockMvc
        .perform(admin(get("/api/activities")).param("categoryId", categoryB.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].categoryId").value(categoryB.toString()));
  }

  @Test
  @DisplayName("US004 AC4: listing without filters returns all statuses including REJECTED")
  void listWithoutFiltersReturnsAll() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    createActivity(club, UUID.randomUUID(), "Active Yoga");
    Activity rejected = createActivity(club, UUID.randomUUID(), "Rejected Yoga");
    rejected.setStatus(ActivityStatus.REJECTED);
    activityRepository.save(rejected);

    mockMvc
        .perform(admin(get("/api/activities")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  @DisplayName("US004 AC5: non-admin list is rejected with 403 FORBIDDEN_OPERATION")
  void nonAdminListIsForbidden() throws Exception {
    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/activities")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US004: filtering with no matches returns an empty page")
  void listReturnsEmptyPage() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    createActivity(club, UUID.randomUUID(), "Morning Yoga");

    mockMvc
        .perform(admin(get("/api/activities")).param("status", "REJECTED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0));
  }

  @Test
  @DisplayName("US005 AC1: admin fetches an activity with details, instructor and status")
  void adminGetsActivityDetail() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");
    ContactPerson instructor = new ContactPerson();
    instructor.setFirstName("Alex");
    instructor.setLastName("Martin");
    instructor.setEmail("alex@zen.example");
    instructor.setPhoneNumber("+33100000000");
    activity.setInstructor(instructor);
    activityRepository.save(activity);

    mockMvc
        .perform(admin(get("/api/activities/{id}", activity.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(activity.getId().toString()))
        .andExpect(jsonPath("$.name").value("Morning Yoga"))
        .andExpect(jsonPath("$.clubId").value(club.getId().toString()))
        .andExpect(jsonPath("$.categoryId").value(categoryId.toString()))
        .andExpect(jsonPath("$.status").value("ACTIVE"))
        .andExpect(jsonPath("$.instructorFirstName").value("Alex"))
        .andExpect(jsonPath("$.instructorLastName").value("Martin"))
        .andExpect(jsonPath("$.instructorEmail").value("alex@zen.example"))
        .andExpect(jsonPath("$.subscriptionDate").value("2026-11-01"));
  }

  @Test
  @DisplayName("US005 AC2: fetching an unknown activity id returns 404 ACTIVITY_NOT_FOUND")
  void getUnknownActivityIsNotFound() throws Exception {
    mockMvc
        .perform(admin(get("/api/activities/{id}", UUID.randomUUID())))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("ACTIVITY_NOT_FOUND"));
  }

  @Test
  @DisplayName("US005 AC3: non-admin detail fetch is rejected with 403 FORBIDDEN_OPERATION")
  void nonAdminGetIsForbidden() throws Exception {
    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/activities/{id}", UUID.randomUUID())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US006 AC1: internal resolve returns ActivityInfo with club-derived partnerId")
  void internalResolveReturnsActivityInfo() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, true);
    UUID categoryId = UUID.randomUUID();
    Activity activity = createActivity(club, categoryId, "Morning Yoga");

    mockMvc
        .perform(get("/internal/activities/{id}", activity.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(activity.getId().toString()))
        .andExpect(jsonPath("$.clubId").value(club.getId().toString()))
        .andExpect(jsonPath("$.partnerId").value(partner.getId().toString()))
        .andExpect(jsonPath("$.name").value("Morning Yoga"))
        .andExpect(jsonPath("$.categoryId").value(categoryId.toString()))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  @DisplayName("US006 AC2: internal resolve of unknown id returns 404 ACTIVITY_NOT_FOUND")
  void internalResolveUnknownIsNotFound() throws Exception {
    mockMvc
        .perform(get("/internal/activities/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("ACTIVITY_NOT_FOUND"));
  }
}
