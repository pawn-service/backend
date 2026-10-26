package com.spring.backend.catalog.infrastructure.persistence;

import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.entity.CategoryEntity;

/**
 * Maps between domain Category and legacy CategoryEntity (JPA).
 *
 * <p>Temporary: uses the legacy CategoryEntity until Product is also migrated into the catalog
 * context. At that point, a dedicated CategoryJpaEntity replaces it.
 */
public final class CategoryPersistenceMapper {

  private CategoryPersistenceMapper() {}

  /** Legacy JPA entity → domain aggregate. */
  public static Category toDomain(CategoryEntity entity) {
    return Category.reconstitute(
        entity.getId(),
        entity.getName(),
        entity.getNote(),
        Boolean.TRUE.equals(entity.getIsActive()),
        entity.getCreatedBy(),
        entity.getCreatedAt(),
        entity.getUpdatedBy(),
        entity.getUpdatedAt());
  }

  /** Domain aggregate → legacy JPA entity (for create). */
  public static CategoryEntity toNewEntity(Category domain) {
    return CategoryEntity.builder()
        .name(domain.getName())
        .note(domain.getNote())
        .isActive(domain.isActive())
        .build();
  }

  /** Apply domain state onto an existing JPA entity (for update). */
  public static void applyToEntity(Category domain, CategoryEntity entity) {
    entity.setName(domain.getName());
    entity.setNote(domain.getNote());
    entity.setIsActive(domain.isActive());
  }
}
