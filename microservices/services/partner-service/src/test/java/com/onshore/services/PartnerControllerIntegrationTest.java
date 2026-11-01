package com.onshore.services;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onshore.common_lib.enums.UserRole;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** Integration tests for the PartnerController REST API. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class PartnerControllerIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  private static final UUID OWNER = UUID.randomUUID();

  @Autowired private MockMvc mockMvc;
  @Autowired private PartnerRepository partnerRepository;

  @BeforeEach
  void cleanUp() {
    partnerRepository.deleteAll();
  }

  private static String body(String name, String street, String city, String zipCode) {
    return "{\"name\":\"%s\",\"street\":\"%s\",\"city\":\"%s\",\"zipCode\":\"%s\"}"
        .formatted(name, street, city, zipCode);
  }

  private MockHttpServletRequestBuilder as(String role, MockHttpServletRequestBuilder request) {
    return request.header("X-User-Roles", role).contentType(MediaType.APPLICATION_JSON);
  }

  private MockHttpServletRequestBuilder asUser(MockHttpServletRequestBuilder request, UUID userId) {
    return request.header("X-User-Id", userId).contentType(MediaType.APPLICATION_JSON);
  }

  private void createPartner(UUID ownerUserId) throws Exception {
    createPartner(ownerUserId, "Yoga Studio");
  }

  private void createPartner(UUID ownerUserId, String name) throws Exception {
    mockMvc
        .perform(
            asUser(post("/api/partners"), ownerUserId)
                .content(body(name, "Main Street 1", "Paris", "75001")))
        .andExpect(status().isCreated());
  }

  private UUID partnerId(UUID ownerUserId) {
    return partnerRepository.findByOwnerUserId(ownerUserId).get(0).getId();
  }

  @Test
  @DisplayName("POST /api/partners creates a PENDING partner for the owner")
  void signupCreatesPendingPartner() throws Exception {
    mockMvc
        .perform(
            asUser(post("/api/partners"), OWNER)
                .content(body("Yoga Studio", "Main Street 1", "Paris", "75001")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Yoga Studio"))
        .andExpect(jsonPath("$.city").value("Paris"))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.ownerUserId").value(OWNER.toString()));
  }

  @Test
  @DisplayName("POST allows an owner to create a second partner")
  void ownerCanCreateSecondPartner() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(
            asUser(post("/api/partners"), OWNER)
                .content(body("Pilates Club", "Avenue 2", "Lyon", "69000")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Pilates Club"));
  }

  @Test
  @DisplayName("POST without a required field returns 400")
  void missingRequiredFieldIsRejected() throws Exception {
    mockMvc
        .perform(
            asUser(post("/api/partners"), OWNER)
                .content("{\"name\":\"Yoga Studio\",\"city\":\"Paris\",\"zipCode\":\"75001\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_PAYLOAD"));
  }

  @Test
  @DisplayName("GET /api/partners/{id} returns the partner")
  void getReturnsPartner() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(get("/api/partners/{id}", partnerId(OWNER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Yoga Studio"))
        .andExpect(jsonPath("$.city").value("Paris"));
  }

  @Test
  @DisplayName("GET /api/partners/{id} on an unknown id returns 404")
  void getUnknownPartnerIsNotFound() throws Exception {
    mockMvc
        .perform(get("/api/partners/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PARTNER_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/partners/mine returns the owner's partners")
  void mineReturnsPartner() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(asUser(get("/api/partners/mine"), OWNER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Yoga Studio"));
  }

  @Test
  @DisplayName("GET /api/partners/mine without a partner returns an empty list")
  void mineWithoutPartnerReturnsEmptyList() throws Exception {
    mockMvc
        .perform(asUser(get("/api/partners/mine"), OWNER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/partners hides non-ACTIVE partners from members")
  void memberListShowsOnlyActivePartners() throws Exception {
    createPartner(OWNER);
    UUID otherOwner = UUID.randomUUID();
    createPartner(otherOwner, "Pilates Club");

    mockMvc
        .perform(
            as(
                    UserRole.ROLE_SYSTEM_ADMIN.name(),
                    patch("/api/partners/{id}/status", partnerId(otherOwner))
                        .param("status", "ACTIVE"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/partners")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].status").value("ACTIVE"));

    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/partners")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @DisplayName("GET /api/partners filters by city")
  void listFiltersByCity() throws Exception {
    createPartner(OWNER);
    UUID otherOwner = UUID.randomUUID();
    mockMvc
        .perform(
            asUser(post("/api/partners"), otherOwner)
                .content(body("Pilates Club", "Avenue 2", "Lyon", "69000")))
        .andExpect(status().isCreated());

    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/partners")).param("city", "lyon"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Pilates Club"));
  }

  @Test
  @DisplayName("PUT by the owner updates the partner")
  void ownerUpdatesPartner() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(
            asUser(put("/api/partners/{id}", partnerId(OWNER)), OWNER)
                .content(body("Yoga Studio Updated", "New Street 9", "Paris", "75001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Yoga Studio Updated"))
        .andExpect(jsonPath("$.street").value("New Street 9"));
  }

  @Test
  @DisplayName("PUT by a non-owner returns 403")
  void nonOwnerUpdateIsForbidden() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(
            asUser(put("/api/partners/{id}", partnerId(OWNER)), UUID.randomUUID())
                .content(body("Hijacked", "Street", "Paris", "75001")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("PUT by an admin can update another owner's partner")
  void adminUpdatesOthersPartner() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(
            asUser(put("/api/partners/{id}", partnerId(OWNER)), UUID.randomUUID())
                .header("X-User-Roles", UserRole.ROLE_SYSTEM_ADMIN.name())
                .content(body("Admin Renamed", "Street", "Paris", "75001")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Admin Renamed"));
  }

  @Test
  @DisplayName("PATCH /api/partners/{id}/status by an admin changes the status")
  void adminChangesStatus() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(
            as(
                    UserRole.ROLE_SYSTEM_ADMIN.name(),
                    patch("/api/partners/{id}/status", partnerId(OWNER)).param("status", "ACTIVE"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  @DisplayName("PATCH /api/partners/{id}/status by a member returns 403")
  void memberStatusChangeIsForbidden() throws Exception {
    createPartner(OWNER);

    mockMvc
        .perform(
            as(
                    UserRole.ROLE_MEMBER.name(),
                    patch("/api/partners/{id}/status", partnerId(OWNER)).param("status", "ACTIVE"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN_OPERATION"));
  }

  @Test
  @DisplayName("PATCH /api/partners/{id}/status on an unknown id returns 404")
  void statusChangeOnUnknownPartnerIsNotFound() throws Exception {
    mockMvc
        .perform(
            as(
                    UserRole.ROLE_SYSTEM_ADMIN.name(),
                    patch("/api/partners/{id}/status", UUID.randomUUID()).param("status", "ACTIVE"))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PARTNER_NOT_FOUND"));
  }
}
