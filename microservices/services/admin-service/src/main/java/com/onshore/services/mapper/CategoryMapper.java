package com.onshore.services.mapper;

import com.onshore.common_lib.club.CategoryInfo;
import com.onshore.services.model.Category;
import com.onshore.services.payload.CategoryRequest;
import com.onshore.services.payload.CategoryResponse;
import java.util.Locale;

public class CategoryMapper {

  private CategoryMapper() {}

  public static String normalizeCode(String code) {
    return code.trim().toUpperCase(Locale.ROOT);
  }

  /** The image is left unset: the service stores the upload and sets its URL. */
  public static Category toEntity(CategoryRequest request) {
    return Category.builder()
        .code(normalizeCode(request.code()))
        .name(request.name().trim())
        .description(trimOrNull(request.description()))
        .build();
  }

  /** Never touches the code or the image. */
  public static void update(Category entity, CategoryRequest request) {
    if (request.name() != null) entity.setName(request.name().trim());
    if (request.description() != null) entity.setDescription(request.description().trim());
  }

  public static CategoryResponse toResponse(Category e) {
    return new CategoryResponse(
        e.getId(), e.getCode(), e.getName(), e.getDescription(), e.getImage(), e.isActive());
  }

  public static CategoryInfo toInfo(Category e) {
    return new CategoryInfo(
        e.getId(), e.getCode(), e.getName(), e.getDescription(), e.getImage(), e.isActive());
  }

  private static String trimOrNull(String value) {
    return value == null ? null : value.trim();
  }
}
