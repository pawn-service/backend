package com.spring.backend.catalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.backend.catalog.domain.model.Category;
import com.spring.backend.entity.CategoryEntity;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CategoryPersistenceMapper")
class CategoryPersistenceMapperTest {

  @Test
  @DisplayName("toDomain maps all fields from JPA entity to domain model")
  void toDomain_allFields_mapped() {
    var now = Instant.now();
    var entity = CategoryEntity.builder().name("Electronics").note("Tech").isActive(true).build();
    // Simulate persisted state
    entity.setId(1L);
    entity.setCreatedBy(10L);
    entity.setCreatedAt(now);
    entity.setUpdatedBy(20L);
    entity.setUpdatedAt(now);

    Category domain = CategoryPersistenceMapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(1L);
    assertThat(domain.getName()).isEqualTo("Electronics");
    assertThat(domain.getNote()).isEqualTo("Tech");
    assertThat(domain.isActive()).isTrue();
    assertThat(domain.getCreatedBy()).isEqualTo(10L);
    assertThat(domain.getCreatedAt()).isEqualTo(now);
    assertThat(domain.getUpdatedBy()).isEqualTo(20L);
    assertThat(domain.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("toDomain handles null isActive as false")
  void toDomain_nullIsActive_false() {
    var entity = CategoryEntity.builder().name("X").isActive(null).build();

    Category domain = CategoryPersistenceMapper.toDomain(entity);

    assertThat(domain.isActive()).isFalse();
  }

  @Test
  @DisplayName("toNewEntity creates JPA entity from domain (no id, no audit fields)")
  void toNewEntity_domainToEntity() {
    Category domain = Category.create("Books", "Reading material");

    CategoryEntity entity = CategoryPersistenceMapper.toNewEntity(domain);

    assertThat(entity.getId()).isNull();
    assertThat(entity.getName()).isEqualTo("Books");
    assertThat(entity.getNote()).isEqualTo("Reading material");
    assertThat(entity.getIsActive()).isTrue();
  }

  @Test
  @DisplayName("applyToEntity updates existing entity fields from domain")
  void applyToEntity_updatesFields() {
    var entity = CategoryEntity.builder().name("Old").note("old note").isActive(true).build();
    entity.setId(5L);

    Category domain = Category.reconstitute(5L, "New", "new note", false, null, null, null, null);

    CategoryPersistenceMapper.applyToEntity(domain, entity);

    assertThat(entity.getName()).isEqualTo("New");
    assertThat(entity.getNote()).isEqualTo("new note");
    assertThat(entity.getIsActive()).isFalse();
    assertThat(entity.getId()).isEqualTo(5L); // id unchanged
  }
}
