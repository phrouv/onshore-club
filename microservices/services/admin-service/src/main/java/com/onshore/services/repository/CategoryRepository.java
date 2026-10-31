package com.onshore.services.repository;

import com.onshore.services.model.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

  Optional<Category> findByCode(String code);

  boolean existsByCode(String code);
}
