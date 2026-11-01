package com.onshore.services;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onshore.common_lib.enums.PartnerStatus;
import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.model.Address;
import com.onshore.common_lib.model.GeoLocation;
import com.onshore.services.model.Club;
import com.onshore.services.model.Partner;
import com.onshore.services.repository.ClubRepository;
import com.onshore.services.repository.PartnerRepository;
import java.math.BigDecimal;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** US001 - Create a Club, AC1 to AC10. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class ClubControllerIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  @Autowired private MockMvc mockMvc;
  @Autowired private ClubRepository clubRepository;
  @Autowired private PartnerRepository partnerRepository;

  @BeforeEach
  void cleanUp() {
    clubRepository.deleteAll();
    partnerRepository.deleteAll();
  }

  private static String body(UUID partnerId, String name) {
    return "{\"partnerId\":\"%s\",\"name\":\"%s\",\"street\":\"Main Street 1\",\"city\":\"Paris\",\"zipCode\":\"75001\"}"
        .formatted(partnerId, name);
  }

  private MockHttpServletRequestBuilder as(String role, MockHttpServletRequestBuilder request) {
    return request.header("X-User-Roles", role).contentType(MediaType.APPLICATION_JSON);
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

  private Club createClub(Partner partner, String name) {
    Address address = new Address();
    address.setStreet("Main Street 1");
    address.setCity("Paris");
    address.setZipCode("75001");
    Club club = new Club();
    club.setPartnerId(partner.getId());
    club.setName(name);
    club.setAddress(address);
    return clubRepository.save(club);
  }

  @Test
  @DisplayName("AC2: admin creates a club with open=true")
  void adminCreatesClub() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/clubs"))
                .content(body(partner.getId(), "Marais")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Marais"))
        .andExpect(jsonPath("$.partnerId").value(partner.getId().toString()))
        .andExpect(jsonPath("$.open").value(true));
  }

  @Test
  @DisplayName("AC3: non-admin create is rejected with 403")
  void nonAdminCreateIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), post("/api/clubs"))
                .content(body(partner.getId(), "Marais")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("AC4: create for a non-ACTIVE partner is rejected with 409")
  void createForInactivePartnerIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.PENDING);

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/clubs"))
                .content(body(partner.getId(), "Marais")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("PARTNER_NOT_ACTIVE"));
  }

  @Test
  @DisplayName("AC5: duplicate name within the same partner is rejected with 409")
  void duplicateNameIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/clubs"))
                .content(body(partner.getId(), "Marais")))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/clubs"))
                .content(body(partner.getId(), "Marais")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("CLUB_ALREADY_EXISTS"));
  }

  @Test
  @DisplayName("AC7: missing mandatory field is rejected with 400")
  void missingMandatoryFieldIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/clubs"))
                .content(
                    "{\"partnerId\":\"%s\",\"street\":\"Main Street 1\",\"city\":\"Paris\",\"zipCode\":\"75001\"}"
                        .formatted(partner.getId())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("US002 AC1/AC2: admin updates a club and can move it to another partner")
  void adminUpdatesClub() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Partner other = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/clubs/{id}", club.getId()))
                .content(body(other.getId(), "Marais Updated")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Marais Updated"))
        .andExpect(jsonPath("$.partnerId").value(other.getId().toString()));
  }

  @Test
  @DisplayName("US002 AC3: non-admin update is rejected with 403")
  void nonAdminUpdateIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), put("/api/clubs/{id}", club.getId()))
                .content(body(partner.getId(), "Marais Updated")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US002 AC4: update on an unknown id returns 404")
  void updateUnknownClubIsNotFound() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/clubs/{id}", UUID.randomUUID()))
                .content(body(partner.getId(), "Marais")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CLUB_NOT_FOUND"));
  }

  @Test
  @DisplayName("US002 AC5: updating to a duplicate name within the partner returns 409")
  void updateToDuplicateNameIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");
    createClub(partner, "Bastille");

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/clubs/{id}", club.getId()))
                .content(body(partner.getId(), "Bastille")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("CLUB_ALREADY_EXISTS"));
  }

  @Test
  @DisplayName("US002 AC7: update with a missing mandatory field returns 400")
  void updateWithMissingFieldIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/clubs/{id}", club.getId()))
                .content(
                    "{\"partnerId\":\"%s\",\"street\":\"Main Street 1\",\"city\":\"Paris\",\"zipCode\":\"75001\"}"
                        .formatted(partner.getId())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("US003 AC1/AC2: admin closes and reopens a club")
  void adminTogglesClub() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/clubs/{id}/open", club.getId()))
                .param("open", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.open").value(false));

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/clubs/{id}/open", club.getId()))
                .param("open", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.open").value(true));
  }

  @Test
  @DisplayName("US003 AC4: non-admin toggle is rejected with 403")
  void nonAdminToggleIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), patch("/api/clubs/{id}/open", club.getId()))
                .param("open", "false"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US003 AC5: toggle on an unknown id returns 404")
  void toggleUnknownClubIsNotFound() throws Exception {
    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/clubs/{id}/open", UUID.randomUUID()))
                .param("open", "true"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CLUB_NOT_FOUND"));
  }

  @Test
  @DisplayName("US003 AC6: toggle with missing or invalid open returns 400")
  void toggleWithInvalidOpenIsRejected() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/clubs/{id}/open", club.getId()))
                .param("open", "not-a-boolean"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));

    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/clubs/{id}/open", club.getId())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("US004 AC1: admin lists all clubs, including closed ones")
  void listReturnsAllClubsIncludingClosed() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    createClub(partner, "Marais");
    Club closed = createClub(partner, "Bastille");
    closed.setOpen(false);
    clubRepository.save(closed);

    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/clubs")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("US004 AC2/AC3: admin filters by partnerId and gets an empty list when none match")
  void listFiltersByPartnerId() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Partner other = createPartner(PartnerStatus.ACTIVE);
    createClub(partner, "Marais");
    createClub(other, "Louvre");

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/clubs"))
                .param("partnerId", partner.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Marais"));

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/clubs"))
                .param("partnerId", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("US004 AC4: admin gets a club detail with address and coordinates")
  void getReturnsClubDetail() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");
    GeoLocation geoLocation = new GeoLocation();
    geoLocation.setLatitude(new BigDecimal("48.8698"));
    geoLocation.setLongitude(new BigDecimal("2.3323"));
    club.setGeoLocation(geoLocation);
    clubRepository.save(club);

    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/clubs/{id}", club.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Marais"))
        .andExpect(jsonPath("$.city").value("Paris"))
        .andExpect(jsonPath("$.latitude").isNumber())
        .andExpect(jsonPath("$.longitude").isNumber());
  }

  @Test
  @DisplayName("US004 AC5: get on an unknown id returns 404")
  void getUnknownClubIsNotFound() throws Exception {
    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/clubs/{id}", UUID.randomUUID())))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CLUB_NOT_FOUND"));
  }

  @Test
  @DisplayName("US004 AC6: non-admin list or get is rejected with 403")
  void nonAdminListOrGetIsForbidden() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");

    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/clubs")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));

    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/clubs/{id}", club.getId())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("US005 AC1: internal resolve returns ClubInfo")
  void internalResolveClubReturnsClubInfo() throws Exception {
    Partner partner = createPartner(PartnerStatus.ACTIVE);
    Club club = createClub(partner, "Marais");
    GeoLocation geoLocation = new GeoLocation();
    geoLocation.setLatitude(new BigDecimal("48.8698"));
    geoLocation.setLongitude(new BigDecimal("2.3323"));
    club.setGeoLocation(geoLocation);
    clubRepository.save(club);

    mockMvc
        .perform(get("/internal/clubs/{id}", club.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(club.getId().toString()))
        .andExpect(jsonPath("$.partnerId").value(partner.getId().toString()))
        .andExpect(jsonPath("$.name").value("Marais"))
        .andExpect(jsonPath("$.city").value("Paris"))
        .andExpect(jsonPath("$.latitude").isNumber())
        .andExpect(jsonPath("$.longitude").isNumber())
        .andExpect(jsonPath("$.open").value(true));
  }

  @Test
  @DisplayName("US005 AC2: internal resolve on an unknown id returns 404")
  void internalResolveUnknownClubIsNotFound() throws Exception {
    mockMvc
        .perform(get("/internal/clubs/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CLUB_NOT_FOUND"));
  }
}
