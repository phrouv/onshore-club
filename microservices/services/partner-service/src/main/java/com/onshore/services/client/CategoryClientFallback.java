package com.onshore.services.client;

import com.onshore.common_lib.club.CategoryInfo;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CategoryClientFallback implements CategoryClient {

  @Override
  public CategoryInfo category(UUID id) {
    return null;
  }
}
