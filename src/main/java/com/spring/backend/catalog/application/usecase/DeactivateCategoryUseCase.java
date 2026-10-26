package com.spring.backend.catalog.application.usecase;

import com.spring.backend.catalog.domain.exception.CategoryNotFoundException;
import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.catalog.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: soft-delete (deactivate) a category.
 *
 * <p>Flow: load active aggregate → call domain behavior → persist.
 *
 * <p>Business rule "cannot deactivate already-inactive category" is enforced by {@link
 * Category#deactivate}.
 */
@Service
@RequiredArgsConstructor
public class DeactivateCategoryUseCase {

  private final CategoryRepository categoryRepository;

  @Transactional
  public void execute(Long categoryId) {
    Category category =
        categoryRepository
            .findActiveById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));

    category.deactivate();

    categoryRepository.save(category);
  }
}
