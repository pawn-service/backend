package com.spring.backend.catalog.infrastructure.persistence;

import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.catalog.domain.repository.CategoryRepository;
import com.spring.backend.entity.CategoryEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Adapts the legacy Spring Data CategoryRepository to the domain CategoryRepository interface.
 *
 * <p>This is the only place that touches JPA for the Category aggregate. The rest of the catalog
 * context works with the pure domain model.
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

  // Legacy Spring Data repository — will be replaced by a dedicated one when Product is migrated.
  private final com.spring.backend.repository.CategoryRepository jpaRepository;

  @Override
  public Category save(Category category) {
    CategoryEntity entity;

    if (category.getId() == null) {
      // New aggregate → create JPA entity
      entity = CategoryPersistenceMapper.toNewEntity(category);
    } else {
      // Existing aggregate → load and update JPA entity
      entity =
          jpaRepository
              .findById(category.getId())
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Category entity not found for id=" + category.getId()));
      CategoryPersistenceMapper.applyToEntity(category, entity);
    }

    CategoryEntity saved = jpaRepository.save(entity);
    return CategoryPersistenceMapper.toDomain(saved);
  }

  @Override
  public Optional<Category> findById(Long id) {
    return jpaRepository.findById(id).map(CategoryPersistenceMapper::toDomain);
  }

  @Override
  public Optional<Category> findActiveById(Long id) {
    return jpaRepository.findByIdAndIsActiveIsTrue(id).map(CategoryPersistenceMapper::toDomain);
  }
}
