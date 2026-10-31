package com.onshore.services.service.impl;

import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import com.onshore.services.mapper.CategoryMapper;
import com.onshore.services.model.Category;
import com.onshore.services.payload.CategoryRequest;
import com.onshore.services.payload.CategoryResponse;
import com.onshore.services.repository.CategoryRepository;
import com.onshore.services.service.CategoryService;
import com.onshore.services.service.ImageStorageService;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private static final int MAX_IMAGE_BYTES = 2 * 1024 * 1024;
  private static final byte[] PNG_MAGIC = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
  private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

  private final CategoryRepository categoryRepository;
  private final ImageStorageService imageStorage;

  @Override
  public CategoryResponse create(CategoryRequest request) {
    String code = CategoryMapper.normalizeCode(request.code());
    if (categoryRepository.existsByCode(code)) {
      throw new ApiException(
          ClubExceptionType.CATEGORY_ALREADY_EXISTS, "Category already exists: " + code);
    }
    Category category = CategoryMapper.toEntity(request);
    category.setImage(storeImage(code, request.image()));
    return CategoryMapper.toResponse(categoryRepository.save(category));
  }

  @Override
  public CategoryResponse update(String code, CategoryRequest request) {
    Category category = find(code);
    if (!category.getCode().equals(CategoryMapper.normalizeCode(request.code()))) {
      throw new ApiException(
          ClubExceptionType.CATEGORY_CODE_IMMUTABLE, "Category code cannot be changed");
    }
    CategoryMapper.update(category, request);
    String previousImage = category.getImage();
    boolean replaced = !request.image().trim().equals(previousImage);
    if (replaced) {
      category.setImage(storeImage(category.getCode(), request.image()));
    }
    CategoryResponse response = CategoryMapper.toResponse(categoryRepository.save(category));
    if (replaced) {
      deleteQuietly(previousImage);
    }
    return response;
  }

  @Override
  public CategoryResponse changeStatus(String code, boolean active) {
    Category category = find(code);
    category.setActive(active);
    return CategoryMapper.toResponse(categoryRepository.save(category));
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryResponse get(String code) {
    return CategoryMapper.toResponse(find(code));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> list(boolean activeOnly) {
    return categoryRepository.findAll().stream()
        .filter(c -> !activeOnly || c.isActive())
        .map(CategoryMapper::toResponse)
        .toList();
  }

  private Category find(String code) {
    String normalized = CategoryMapper.normalizeCode(code);
    return categoryRepository
        .findByCode(normalized)
        .orElseThrow(
            () ->
                new ApiException(
                    ClubExceptionType.CATEGORY_NOT_FOUND, "Category not found: " + normalized));
  }

  /** Accepts a Base64 payload or a data URL holding a JPEG or PNG, and returns its stored URL. */
  private String storeImage(String code, String payload) {
    String base64 = payload.trim();
    if (base64.startsWith("data:")) {
      base64 = base64.substring(base64.indexOf(',') + 1);
    }
    byte[] content;
    try {
      content = Base64.getDecoder().decode(base64);
    } catch (IllegalArgumentException e) {
      throw invalidImage("Image must be a Base64 encoded JPEG or PNG");
    }
    if (content.length > MAX_IMAGE_BYTES) {
      throw invalidImage("Image must not exceed 2 MB");
    }
    String contentType;
    String extension;
    if (startsWith(content, PNG_MAGIC)) {
      contentType = "image/png";
      extension = ".png";
    } else if (startsWith(content, JPEG_MAGIC)) {
      contentType = "image/jpeg";
      extension = ".jpg";
    } else {
      throw invalidImage("Image must be a JPEG or PNG");
    }
    String key = "categories/" + code.toLowerCase() + "-" + System.currentTimeMillis() + extension;
    return imageStorage.upload(key, content, contentType);
  }

  private void deleteQuietly(String imageUrl) {
    try {
      imageStorage.deleteByUrl(imageUrl);
    } catch (RuntimeException e) {
      log.warn("Failed to delete category image {}", imageUrl, e);
    }
  }

  private static boolean startsWith(byte[] content, byte[] prefix) {
    if (content.length < prefix.length) {
      return false;
    }
    for (int i = 0; i < prefix.length; i++) {
      if (content[i] != prefix[i]) {
        return false;
      }
    }
    return true;
  }

  private static ApiException invalidImage(String message) {
    return new ApiException(ClubExceptionType.INVALID_PAYLOAD, message);
  }
}
