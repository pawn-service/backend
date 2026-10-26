package com.spring.backend.catalog.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
@DisplayName("DeactivateCategoryUseCase")
class DeactivateCategoryUseCaseTest {

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private DeactivateCategoryUseCase useCase;

  @Test
  @DisplayName("deactivates active category and saves")
  void execute_activeCategory_deactivatedAndSaved() {
    var now = Instant.now();
    var existing = Category.reconstitute(1L, "Electronics", null, true, 10L, now, 10L, now);

    when(categoryRepository.findActiveById(1L)).thenReturn(Optional.of(existing));

    useCase.execute(1L);

    verify(categoryRepository).save(argThat(c -> !c.isActive()));
  }

  @Test
  @DisplayName("throws CategoryNotFoundException when category not found or already inactive")
  void execute_notFound_throws() {
    when(categoryRepository.findActiveById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.execute(99L))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessageContaining("99");
  }
}
