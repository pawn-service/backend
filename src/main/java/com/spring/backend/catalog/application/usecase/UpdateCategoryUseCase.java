package com.spring.backend.catalog.application.usecase;

import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.dto.CategoryResultMapper;
import com.spring.backend.catalog.application.dto.UpdateCategoryCommand;
import com.spring.backend.catalog.domain.exception.CategoryNotFoundException;
import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.catalog.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: update (rename) a category.
 *
 * <p>Flow: load active aggregate → call domain behavior → persist → return result.
 *
 * <p>Business rule "cannot update inactive category" is enforced by {@link Category#rename} — not
 * here.
 */
@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCase {

  private final CategoryRepository categoryRepository;

  @Transactional
  public CategoryResult execute(UpdateCategoryCommand command) {
    Category category =
        categoryRepository
            .findActiveById(command.id())
            .orElseThrow(() -> new CategoryNotFoundException(command.id()));

    category.rename(command.name(), command.note());

    Category saved = categoryRepository.save(category);
    return CategoryResultMapper.toResult(saved);
  }
}
