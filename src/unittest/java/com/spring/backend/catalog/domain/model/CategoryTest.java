package com.spring.backend.catalog.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Category aggregate")
class CategoryTest {

  @Nested
  @DisplayName("create")
  class Create {

    @Test
    @DisplayName("creates active category with name and note")
    void create_validInput_activeCategory() {
      var category = Category.create("Electronics", "Tech gadgets");

      assertThat(category.getName()).isEqualTo("Electronics");
      assertThat(category.getNote()).isEqualTo("Tech gadgets");
      assertThat(category.isActive()).isTrue();
      assertThat(category.getId()).isNull(); // not persisted yet
    }

    @Test
    @DisplayName("strips whitespace from name")
    void create_whitespace_stripped() {
      var category = Category.create("  Books  ", null);

      assertThat(category.getName()).isEqualTo("Books");
    }

    @Test
    @DisplayName("rejects null name")
    void create_nullName_throws() {
      assertThatThrownBy(() -> Category.create(null, null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("blank");
    }

    @Test
    @DisplayName("rejects blank name")
    void create_blankName_throws() {
      assertThatThrownBy(() -> Category.create("   ", null))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  @DisplayName("rename")
  class Rename {

    @Test
    @DisplayName("changes name and note when active")
    void rename_active_success() {
      var category = Category.create("Old", "old note");

      category.rename("New", "new note");

      assertThat(category.getName()).isEqualTo("New");
      assertThat(category.getNote()).isEqualTo("new note");
    }

    @Test
    @DisplayName("rejects rename when deactivated")
    void rename_inactive_throws() {
      var category = Category.create("Name", null);
      category.deactivate();

      assertThatThrownBy(() -> category.rename("New", null))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("not active");
    }

    @Test
    @DisplayName("rejects blank new name")
    void rename_blankName_throws() {
      var category = Category.create("Name", null);

      assertThatThrownBy(() -> category.rename("", null))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  @DisplayName("deactivate")
  class Deactivate {

    @Test
    @DisplayName("sets active to false")
    void deactivate_active_becomesInactive() {
      var category = Category.create("Name", null);

      category.deactivate();

      assertThat(category.isActive()).isFalse();
    }

    @Test
    @DisplayName("rejects double deactivation")
    void deactivate_alreadyInactive_throws() {
      var category = Category.create("Name", null);
      category.deactivate();

      assertThatThrownBy(category::deactivate).isInstanceOf(IllegalStateException.class);
    }
  }

  @Nested
  @DisplayName("reconstitute")
  class Reconstitute {

    @Test
    @DisplayName("restores all fields from persistence")
    void reconstitute_allFields() {
      var now = Instant.now();
      var category = Category.reconstitute(42L, "Restored", "note", true, 1L, now, 2L, now);

      assertThat(category.getId()).isEqualTo(42L);
      assertThat(category.getName()).isEqualTo("Restored");
      assertThat(category.getNote()).isEqualTo("note");
      assertThat(category.isActive()).isTrue();
      assertThat(category.getCreatedBy()).isEqualTo(1L);
      assertThat(category.getCreatedAt()).isEqualTo(now);
    }
  }

  @Nested
  @DisplayName("equality")
  class Equality {

    @Test
    @DisplayName("equal by id")
    void equals_sameId_equal() {
      var a = Category.reconstitute(1L, "A", null, true, null, null, null, null);
      var b = Category.reconstitute(1L, "B", null, false, null, null, null, null);

      assertThat(a).isEqualTo(b);
      assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("not equal with different ids")
    void equals_differentId_notEqual() {
      var a = Category.reconstitute(1L, "A", null, true, null, null, null, null);
      var b = Category.reconstitute(2L, "A", null, true, null, null, null, null);

      assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("new categories without id are not equal")
    void equals_nullIds_notEqual() {
      var a = Category.create("Same", null);
      var b = Category.create("Same", null);

      assertThat(a).isNotEqualTo(b);
    }
  }
}
