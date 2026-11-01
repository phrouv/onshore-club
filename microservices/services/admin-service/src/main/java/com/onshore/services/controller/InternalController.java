package com.onshore.services.controller;

import com.onshore.common_lib.club.CategoryInfo;
import com.onshore.services.service.CategoryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** Service-to-service endpoints; never routed by the api-gateway. */
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

  private final CategoryService categoryService;

  @GetMapping("/categories/{id}")
  public CategoryInfo category(@PathVariable UUID id) {
    return categoryService.info(id);
  }
}
