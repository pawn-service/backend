package com.spring.backend.catalog.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.dto.UpdateCategoryCommand;
import com.spring.backend.catalog.domain.exception.CategoryNotFoundException;
import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.catalog.domain.repository.CategoryRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCategoryUseCase")
class UpdateCategoryUseCaseTest {

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private UpdateCategoryUseCase useCase;

  @Test
  @DisplayName("renames active category and returns updated result")
  void execute_activeCategory_renamedAndSaved() {
    var now = Instant.now();
    var existing = Category.reconstitute(1L, "Old", "old note", true, 10L, now, 10L, now);

    when(categoryRepository.findActiveById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(any(Category.class)))
        .thenAnswer(
            inv -> {
              Category c = inv.getArgument(0);
              return Category.reconstitute(
                  1L, c.getName(), c.getNote(), c.isActive(), 10L, now, 10L, now);
            });

    var command = new UpdateCategoryCommand(1L, "New", "new note");
    CategoryResult result = useCase.execute(command);

    assertThat(result.name()).isEqualTo("New");
    assertThat(result.note()).isEqualTo("new note");
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  @DisplayName("throws CategoryNotFoundException when category not found")
  void execute_notFound_throws() {
    when(categoryRepository.findActiveById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(new UpdateCategoryCommand(99L, "X", null)))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessageContaining("99");
  }
}
