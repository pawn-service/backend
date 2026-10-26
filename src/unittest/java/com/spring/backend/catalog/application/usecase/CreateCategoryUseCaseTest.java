package com.spring.backend.catalog.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.dto.CreateCategoryCommand;
import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.catalog.domain.repository.CategoryRepository;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCategoryUseCase")
class CreateCategoryUseCaseTest {

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private CreateCategoryUseCase useCase;

  @Test
  @DisplayName("creates category and returns result with persisted id")
  void execute_validCommand_returnsSavedResult() {
    var command = new CreateCategoryCommand("Electronics", "Tech gadgets");
    var now = Instant.now();

    // Repository returns the saved aggregate with id and audit fields
    when(categoryRepository.save(any(Category.class)))
        .thenAnswer(
            invocation -> {
              Category input = invocation.getArgument(0);
              return Category.reconstitute(
                  1L, input.getName(), input.getNote(), input.isActive(), 10L, now, 10L, now);
            });

    CategoryResult result = useCase.execute(command);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("Electronics");
    assertThat(result.note()).isEqualTo("Tech gadgets");
    assertThat(result.active()).isTrue();
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  @DisplayName("rejects blank name — domain invariant propagates")
  void execute_blankName_throws() {
    var command = new CreateCategoryCommand("   ", null);

    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("blank");
  }
}
