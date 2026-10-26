package com.spring.backend.catalog.infrastructure.persistence;

import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.dto.CategoryResultMapper;
import com.spring.backend.catalog.application.port.out.CategoryQueryPort;
import com.spring.backend.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Read-only adapter for category queries. Maps legacy JPA entities directly to CategoryResult
 * without going through the domain model (CQRS-lite: queries skip aggregates).
 */
@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {

  private final com.spring.backend.repository.CategoryRepository jpaRepository;

  @Override
  public Optional<CategoryResult> findById(Long id) {
    return jpaRepository.findById(id).map(this::toResult);
  }

  @Override
  public List<CategoryResult> findAll() {
    return jpaRepository.findAll().stream().map(this::toResult).toList();
  }

  @Override
  public Page<CategoryResult> searchByName(String name, Pageable pageable) {
    Specification<CategoryEntity> spec =
        com.spring.backend.repository.CategoryRepository.search(name);
    return jpaRepository.findAll(spec, pageable).map(this::toResult);
  }

  private CategoryResult toResult(CategoryEntity entity) {
    return CategoryResultMapper.fromRaw(
        entity.getId(),
        entity.getName(),
        entity.getNote(),
        Boolean.TRUE.equals(entity.getIsActive()),
        entity.getCreatedBy(),
        entity.getCreatedAt(),
        entity.getUpdatedBy(),
        entity.getUpdatedAt());
  }
}
