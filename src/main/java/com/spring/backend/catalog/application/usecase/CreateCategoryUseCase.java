package com.spring.backend.catalog.application.usecase;

import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.dto.CategoryResultMapper;
import com.spring.backend.catalog.application.dto.CreateCategoryCommand;
import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.catalog.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: create a new category.
 *
 * <p>Flow: validate command → create domain aggregate → persist → return result.
 *
 * <p>No business rules here — they live inside {@link Category#create}.
 */
@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {

  private final CategoryRepository categoryRepository;

  @Transactional
  public CategoryResult execute(CreateCategoryCommand command) {
    Category category = Category.create(command.name(), command.note());
    Category saved = categoryRepository.save(category);
    return CategoryResultMapper.toResult(saved);
  }
}
