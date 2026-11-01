package com.onshore.services.service;

import com.onshore.common_lib.club.CategoryInfo;
import com.onshore.services.payload.CategoryRequest;
import com.onshore.services.payload.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

  CategoryResponse create(CategoryRequest request);

  CategoryResponse update(String code, CategoryRequest request);

  CategoryResponse changeStatus(String code, boolean active);

  CategoryResponse get(String code);

  CategoryInfo info(UUID id);

  List<CategoryResponse> list(boolean activeOnly);
}
