package com.onshore.services.controller;

import com.onshore.common_lib.enums.UserRole;
import com.onshore.services.payload.CategoryRequest;
import com.onshore.services.payload.CategoryResponse;
import com.onshore.services.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<CategoryResponse> create(
      @Valid @RequestBody CategoryRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
  }

  @PutMapping("/{code}")
  public CategoryResponse update(
      @PathVariable String code,
      @Valid @RequestBody CategoryRequest request,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return categoryService.update(code, request);
  }

  @GetMapping
  public List<CategoryResponse> list(
      @RequestParam(required = false) Boolean active,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    // Only admins may list inactive categories.
    boolean activeOnly = !UserRole.isAdmin(roles) || Boolean.TRUE.equals(active);
    return categoryService.list(activeOnly);
  }

  @GetMapping("/{code}")
  public CategoryResponse get(@PathVariable String code) {
    return categoryService.get(code);
  }

  @PatchMapping("/{code}/status")
  public CategoryResponse changeStatus(
      @PathVariable String code,
      @RequestParam boolean active,
      @RequestHeader(value = "X-User-Roles", required = false) String roles) {
    UserRole.requireAdmin(roles);
    return categoryService.changeStatus(code, active);
  }
}
