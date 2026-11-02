package com.onshore.services.client;

import com.onshore.common_lib.club.CategoryInfo;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "admin-service", fallback = CategoryClientFallback.class)
public interface CategoryClient {

  @GetMapping("/internal/categories/{id}")
  CategoryInfo category(@PathVariable("id") UUID id);
}
