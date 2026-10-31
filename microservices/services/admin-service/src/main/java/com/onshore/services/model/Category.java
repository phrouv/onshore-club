package com.onshore.services.model;

import static com.onshore.services.config.Constants.CODE_PATTERN;

import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "categories",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"code"},
          name = "uk_category_code")
    })
public class Category extends AbstractFullAuditingEntity {

  /** Trimmed, upper-case and immutable. */
  @NotNull(message = "code is required")
  @Size(max = 10, message = "code must not exceed 10 characters")
  @Pattern(regexp = CODE_PATTERN, message = "Code must only contain capital letters")
  @Column(name = "code", length = 10, nullable = false)
  private String code;

  @NotNull(message = "name is required")
  @Size(max = 100, message = "name must not exceed 100 characters")
  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Builder.Default
  @Column(name = "active", nullable = false)
  private boolean active = true;

  /** Object key of the category image in the MinIO bucket. */
  @NotNull(message = "image is required")
  @Size(max = 100, message = "image must not exceed 100 characters")
  @Column(name = "image", length = 500, nullable = false)
  private String image;
}
