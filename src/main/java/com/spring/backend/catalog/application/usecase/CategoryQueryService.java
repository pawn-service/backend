package com.spring.backend.catalog.application.usecase;

import com.spring.backend.catalog.application.dto.CategoryDetailResult;
import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.port.out.CategoryQueryPort;
import com.spring.backend.catalog.application.port.out.CreatorLookupPort;
import com.spring.backend.catalog.application.port.out.CreatorLookupPort.CreatorInfo;
import com.spring.backend.catalog.domain.exception.CategoryNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-only query service for categories (CQRS-lite).
 *
 * <p>Queries skip the domain aggregate — they go through {@link CategoryQueryPort} directly to the
 * database, then enrich with creator info from {@link CreatorLookupPort}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {

  private final CategoryQueryPort categoryQueryPort;
  private final CreatorLookupPort creatorLookupPort;

  public CategoryDetailResult getById(Long id) {
    CategoryResult result =
        categoryQueryPort.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

    CreatorInfo creator =
        result.createdBy() != null
            ? creatorLookupPort.findById(result.createdBy()).orElse(null)
            : null;

    return new CategoryDetailResult(result, creator);
  }

  public List<CategoryDetailResult> getAll() {
    List<CategoryResult> categories = categoryQueryPort.findAll();
    Map<Long, CreatorInfo> creators = lookupCreators(categories);

    return categories.stream()
        .map(c -> new CategoryDetailResult(c, findCreator(creators, c)))
        .toList();
  }

  public Page<CategoryDetailResult> search(String name, int page, int size) {
    Page<CategoryResult> categoryPage =
        categoryQueryPort.searchByName(name, PageRequest.of(page, size));

    Map<Long, CreatorInfo> creators = lookupCreators(categoryPage.getContent());

    List<CategoryDetailResult> enriched =
        categoryPage.getContent().stream()
            .map(c -> new CategoryDetailResult(c, findCreator(creators, c)))
            .toList();

    return new PageImpl<>(enriched, categoryPage.getPageable(), categoryPage.getTotalElements());
  }

  /** Bulk lookup to avoid N+1 queries. */
  private Map<Long, CreatorInfo> lookupCreators(List<CategoryResult> categories) {
    Set<Long> creatorIds =
        categories.stream()
            .map(CategoryResult::createdBy)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    return creatorLookupPort.findByIds(creatorIds);
  }

  private static CreatorInfo findCreator(Map<Long, CreatorInfo> creators, CategoryResult c) {
    return c.createdBy() != null ? creators.get(c.createdBy()) : null;
  }
}
