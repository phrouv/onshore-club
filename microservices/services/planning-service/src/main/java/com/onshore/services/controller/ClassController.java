package com.onshore.services.controller;

import com.onshore.common_lib.club.ClassInfo;
import com.onshore.common_lib.enums.ClassStatus;
import com.onshore.services.payload.ClassRequest;
import com.onshore.services.payload.ClassResponse;
import com.onshore.services.service.ClassService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ClassController {

  private final ClassService classService;

  @GetMapping("/api/classes")
  public List<ClassResponse> list(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) UUID activityId,
      @RequestParam(required = false) UUID partnerId,
      @RequestParam(required = false) ClassStatus status,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return classService.list(from, to, activityId, partnerId, status, roles);
  }

  @GetMapping("/api/classes/{id}")
  public ClassResponse get(
      @PathVariable UUID id,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return classService.get(id, roles);
  }

  @PostMapping("/api/classes")
  public ResponseEntity<List<ClassResponse>> create(
      @Valid @RequestBody ClassRequest request,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(classService.create(request, userId, roles));
  }

  @PutMapping("/api/classes/{id}")
  public ClassResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody ClassRequest request,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return classService.update(id, request, userId, roles);
  }

  @PostMapping("/api/classes/{id}/close")
  public ClassResponse close(
      @PathVariable UUID id,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return classService.close(id, userId, roles);
  }

  @PostMapping("/api/classes/{id}/reopen")
  public ClassResponse reopen(
      @PathVariable UUID id,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    return classService.reopen(id, userId, roles);
  }

  @DeleteMapping("/api/classes/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID id,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    classService.delete(id, userId, roles);
    return ResponseEntity.noContent().build();
  }

  /** Service-to-service; never routed by the api-gateway. */
  @GetMapping("/internal/classes/{id}")
  public ClassInfo info(@PathVariable UUID id) {
    return classService.info(id);
  }
}
