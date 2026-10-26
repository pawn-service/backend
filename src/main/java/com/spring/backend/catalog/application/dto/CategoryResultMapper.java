package com.spring.backend.catalog.application.dto;

import com.spring.backend.catalog.domain.model.Category;
import java.time.Instant;

/** Maps domain Category → application CategoryResult. */
public final class CategoryResultMapper {

  private CategoryResultMapper() {}

  /** From domain aggregate (used by command use cases). */
  public static CategoryResult toResult(Category category) {
    return new CategoryResult(
        category.getId(),
        category.getName(),
        category.getNote(),
        category.isActive(),
        category.getCreatedBy(),
        category.getCreatedAt(),
        category.getUpdatedBy(),
        category.getUpdatedAt());
  }

  /** From raw fields (used by query adapters that skip the domain model). */
  public static CategoryResult fromRaw(
      Long id,
      String name,
      String note,
      boolean active,
      Long createdBy,
      Instant createdAt,
      Long updatedBy,
      Instant updatedAt) {
    return new CategoryResult(id, name, note, active, createdBy, createdAt, updatedBy, updatedAt);
  }
}
