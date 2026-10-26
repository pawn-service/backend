package com.spring.backend.catalog.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

import com.spring.backend.catalog.application.dto.CategoryDetailResult;
import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.port.out.CategoryQueryPort;
import com.spring.backend.catalog.application.port.out.CreatorLookupPort;
import com.spring.backend.catalog.application.port.out.CreatorLookupPort.CreatorInfo;
import com.spring.backend.catalog.domain.exception.CategoryNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryQueryService")
class CategoryQueryServiceTest {

  @Mock private CategoryQueryPort categoryQueryPort;
  @Mock private CreatorLookupPort creatorLookupPort;

  @InjectMocks private CategoryQueryService queryService;

  private static final Instant NOW = Instant.now();

  private CategoryResult sampleResult(Long id, Long createdBy) {
    return new CategoryResult(id, "Cat " + id, "note", true, createdBy, NOW, null, NOW);
  }

  private CreatorInfo sampleCreator(Long id) {
    return new CreatorInfo(
        id,
        "user@test.com",
        "User " + id,
        "user" + id,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  @Nested
  @DisplayName("getById")
  class GetById {

    @Test
    @DisplayName("returns category with creator info")
    void getById_found_returnsDetailWithCreator() {
      when(categoryQueryPort.findById(1L)).thenReturn(Optional.of(sampleResult(1L, 10L)));
      when(creatorLookupPort.findById(10L)).thenReturn(Optional.of(sampleCreator(10L)));

      CategoryDetailResult detail = queryService.getById(1L);

      assertThat(detail.category().name()).isEqualTo("Cat 1");
      assertThat(detail.createdByUser()).isNotNull();
      assertThat(detail.createdByUser().name()).isEqualTo("User 10");
    }

    @Test
    @DisplayName("returns null creator when createdBy is null")
    void getById_noCreator_nullCreatorInfo() {
      when(categoryQueryPort.findById(1L)).thenReturn(Optional.of(sampleResult(1L, null)));

      CategoryDetailResult detail = queryService.getById(1L);

      assertThat(detail.createdByUser()).isNull();
    }

    @Test
    @DisplayName("throws CategoryNotFoundException when not found")
    void getById_notFound_throws() {
      when(categoryQueryPort.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> queryService.getById(99L))
          .isInstanceOf(CategoryNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("getAll")
  class GetAll {

    @Test
    @DisplayName("returns all categories enriched with creators (bulk lookup)")
    void getAll_withCreators_enriched() {
      when(categoryQueryPort.findAll())
          .thenReturn(List.of(sampleResult(1L, 10L), sampleResult(2L, 20L)));
      when(creatorLookupPort.findByIds(anySet()))
          .thenReturn(Map.of(10L, sampleCreator(10L), 20L, sampleCreator(20L)));

      List<CategoryDetailResult> results = queryService.getAll();

      assertThat(results).hasSize(2);
      assertThat(results.get(0).createdByUser().name()).isEqualTo("User 10");
      assertThat(results.get(1).createdByUser().name()).isEqualTo("User 20");
    }
  }

  @Nested
  @DisplayName("search")
  class Search {

    @Test
    @DisplayName("returns paged results with creators")
    void search_paged_enriched() {
      var page = new PageImpl<>(List.of(sampleResult(1L, 10L)), PageRequest.of(0, 10), 1);
      when(categoryQueryPort.searchByName(any(), any())).thenReturn(page);
      when(creatorLookupPort.findByIds(anySet())).thenReturn(Map.of(10L, sampleCreator(10L)));

      Page<CategoryDetailResult> results = queryService.search("Cat", 0, 10);

      assertThat(results.getContent()).hasSize(1);
      assertThat(results.getTotalElements()).isEqualTo(1);
      assertThat(results.getContent().getFirst().createdByUser()).isNotNull();
    }
  }
}
