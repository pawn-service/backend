package com.spring.backend.catalog.domain.repository;

import com.spring.backend.catalog.domain.model.Category;
import java.util.Optional;

/**
 * Repository interface for the Category aggregate. Lives in domain — speaks domain language, no
 * Spring types.
 *
 * <p>Implementation lives in infrastructure (CategoryRepositoryAdapter).
 */
public interface CategoryRepository {

  Category save(Category category);

  Optional<Category> findById(Long id);

  Optional<Category> findActiveById(Long id);
}
