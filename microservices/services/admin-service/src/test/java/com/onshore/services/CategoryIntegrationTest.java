package com.onshore.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.onshore.common_lib.enums.UserRole;
import com.onshore.services.repository.CategoryRepository;
import com.onshore.services.service.ImageStorageService;
import java.util.Base64;
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

/** US001 - Manage Category Records, AC1 to AC8. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class CategoryIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

  private static final String IMAGE_BASE_URL = "http://localhost:9000/onshorebucket/";
  private static final String PNG =
      dataUrl("image/png", new byte[] {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0});
  private static final String JPEG =
      dataUrl("image/jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0});

  @Autowired private MockMvc mockMvc;
  @Autowired private CategoryRepository categoryRepository;
  @MockitoBean private ImageStorageService imageStorage;

  @BeforeEach
  void cleanUp() {
    categoryRepository.deleteAll();
    reset(imageStorage);
    when(imageStorage.upload(anyString(), any(), anyString()))
        .thenAnswer(invocation -> IMAGE_BASE_URL + invocation.getArgument(0));
  }

  private static String dataUrl(String contentType, byte[] content) {
    return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(content);
  }

  private String storedImage() {
    return categoryRepository.findByCode("YOGA").orElseThrow().getImage();
  }

  private static String body(String code, String name, String description, String image) {
    String desc = description == null ? "null" : "\"" + description + "\"";
    return "{\"code\":\"%s\",\"name\":\"%s\",\"description\":%s,\"image\":\"%s\"}"
        .formatted(code, name, desc, image);
  }

  private MockHttpServletRequestBuilder as(String role, MockHttpServletRequestBuilder request) {
    return request.header("X-User-Roles", role).contentType(MediaType.APPLICATION_JSON);
  }

  private void createYoga() throws Exception {
    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/categories"))
                .content(body("YOGA", "Yoga", "Yoga classes", PNG)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("AC1: admin creates a category with an unused code")
  void adminCreatesCategory() throws Exception {
    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/categories"))
                .content(body("yoga", "Yoga", "Yoga classes", PNG)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("YOGA"))
        .andExpect(jsonPath("$.active").value(true));

    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/categories")).param("active", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].code").value("YOGA"));
  }

  @Test
  @DisplayName("AC6: creating a category uploads the image and stores its URL")
  void createRetainsImage() throws Exception {
    createYoga();

    assertThat(storedImage()).startsWith(IMAGE_BASE_URL + "categories/yoga-").endsWith(".png");
    verify(imageStorage).upload(anyString(), any(), eq("image/png"));
  }

  @Test
  @DisplayName("AC2: duplicate code after trimming and case folding is rejected with 409")
  void duplicateCodeIsRejected() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/categories"))
                .content(body(" yoga ", "Other", null, PNG)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("AC3: admin updates name and description, code unchanged")
  void adminUpdatesNameAndDescription() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/categories/YOGA"))
                .content(body("YOGA", "Hot Yoga", "Heated classes", storedImage())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("YOGA"))
        .andExpect(jsonPath("$.name").value("Hot Yoga"))
        .andExpect(jsonPath("$.description").value("Heated classes"));
  }

  @Test
  @DisplayName("AC7: PUT replaces the image, deletes the old one, code unchanged")
  void putReplacesImage() throws Exception {
    createYoga();
    String previous = storedImage();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/categories/YOGA"))
                .content(body("YOGA", "Yoga", "Yoga classes", JPEG)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("YOGA"));

    assertThat(storedImage()).isNotEqualTo(previous).endsWith(".jpg");
    verify(imageStorage).deleteByUrl(previous);
  }

  @Test
  @DisplayName("AC7: PUT with the current image URL keeps the image")
  void putWithCurrentUrlKeepsImage() throws Exception {
    createYoga();
    String previous = storedImage();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/categories/YOGA"))
                .content(body("YOGA", "Yoga", "Yoga classes", previous)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.image").value(previous));

    assertThat(storedImage()).isEqualTo(previous);
    verify(imageStorage, never()).deleteByUrl(anyString());
  }

  @Test
  @DisplayName("AC7: description not sent is left unchanged on PUT")
  void putKeepsDescriptionWhenNotSent() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/categories/YOGA"))
                .content(body("YOGA", "Yoga", null, storedImage())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Yoga classes"));
  }

  @Test
  @DisplayName("AC8: non-admin PUT is rejected with 403 and image is unchanged")
  void nonAdminPutIsForbidden() throws Exception {
    createYoga();
    String previous = storedImage();

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), put("/api/categories/YOGA"))
                .content(body("YOGA", "Yoga", "Yoga classes", JPEG)))
        .andExpect(status().isForbidden());

    assertThat(storedImage()).isEqualTo(previous);
  }

  @Test
  @DisplayName("AC4: attempted code change is rejected and the code is unchanged")
  void codeChangeIsRejected() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/categories/YOGA"))
                .content(body("PILATES", "Yoga", "Yoga classes", storedImage())))
        .andExpect(status().isBadRequest());

    assertThat(categoryRepository.findByCode("YOGA")).isPresent();
    assertThat(categoryRepository.findByCode("PILATES")).isEmpty();
  }

  @Test
  @DisplayName("AC5: non-admin create is rejected with 403")
  void nonAdminCreateIsForbidden() throws Exception {
    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), post("/api/categories"))
                .content(body("YOGA", "Yoga", null, PNG)))
        .andExpect(status().isForbidden());

    assertThat(categoryRepository.count()).isZero();
  }

  @Test
  @DisplayName("Image that is not a JPEG or PNG is rejected with 400")
  void nonImageIsRejected() throws Exception {
    String text = dataUrl("text/plain", "hello".getBytes());

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/categories"))
                .content(body("YOGA", "Yoga", null, text)))
        .andExpect(status().isBadRequest());

    assertThat(categoryRepository.count()).isZero();
    verify(imageStorage, never()).upload(anyString(), any(), anyString());
  }

  @Test
  @DisplayName("Image larger than 2 MB is rejected with 400")
  void oversizedImageIsRejected() throws Exception {
    byte[] big = new byte[2 * 1024 * 1024 + 1];
    big[0] = (byte) 0xFF;
    big[1] = (byte) 0xD8;
    big[2] = (byte) 0xFF;

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/categories"))
                .content(body("YOGA", "Yoga", null, dataUrl("image/jpeg", big))))
        .andExpect(status().isBadRequest());

    assertThat(categoryRepository.count()).isZero();
  }

  @Test
  @DisplayName("Another image URL than the current one is rejected with 400")
  void foreignUrlIsRejected() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), put("/api/categories/YOGA"))
                .content(body("YOGA", "Yoga", "Yoga classes", "http://evil.example/x.png")))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Create without an image is rejected with 400")
  void missingImageIsRejected() throws Exception {
    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), post("/api/categories"))
                .content(body("YOGA", "Yoga", null, "")))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Admin deactivates then reactivates a category")
  void adminChangesStatus() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/categories/YOGA/status"))
                .param("active", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(false));
    assertThat(categoryRepository.findByCode("YOGA").orElseThrow().isActive()).isFalse();

    mockMvc
        .perform(as(UserRole.ROLE_MEMBER.name(), get("/api/categories")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
    mockMvc
        .perform(as(UserRole.ROLE_SYSTEM_ADMIN.name(), get("/api/categories")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));

    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/categories/YOGA/status"))
                .param("active", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  @DisplayName("Non-admin status change is rejected with 403 and the status is unchanged")
  void nonAdminStatusChangeIsForbidden() throws Exception {
    createYoga();

    mockMvc
        .perform(
            as(UserRole.ROLE_MEMBER.name(), patch("/api/categories/YOGA/status"))
                .param("active", "false"))
        .andExpect(status().isForbidden());

    assertThat(categoryRepository.findByCode("YOGA").orElseThrow().isActive()).isTrue();
  }

  @Test
  @DisplayName("Status change on an unknown category returns 404")
  void statusChangeOnUnknownCategoryIsNotFound() throws Exception {
    mockMvc
        .perform(
            as(UserRole.ROLE_SYSTEM_ADMIN.name(), patch("/api/categories/NOPE/status"))
                .param("active", "false"))
        .andExpect(status().isNotFound());
  }
}
