package com.onshore.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onshore.common_lib.club.CategoryInfo;
import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.model.Address;
import com.onshore.services.client.CategoryClient;
import com.onshore.services.model.Club;
import com.onshore.services.model.Partner;
import com.onshore.services.repository.ActivityRepository;
import com.onshore.services.repository.ClubRepository;
import com.onshore.services.repository.PartnerRepository;
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

/** US001 - Create an Activity, AC1 to AC11. */
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
}
