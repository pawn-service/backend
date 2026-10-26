package com.spring.backend.catalog.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root for Category.
 *
 * <p>Invariants:
 *
 * <ul>
 *   <li>Name must not be null or blank.
 *   <li>A deactivated category cannot be renamed or updated.
 * </ul>
 */
public class Category {

  private Long id;
  private String name;
  private String note;
  private boolean active;
  private Long createdBy;
  private Instant createdAt;
  private Long updatedBy;
  private Instant updatedAt;

  // ── Factory: create new ──

  public static Category create(String name, String note) {
    requireValidName(name);
    var category = new Category();
    category.name = name.strip();
    category.note = note;
    category.active = true;
    return category;
  }

  // ── Factory: reconstitute from persistence ──

  public static Category reconstitute(
      Long id,
      String name,
      String note,
      boolean active,
      Long createdBy,
      Instant createdAt,
      Long updatedBy,
      Instant updatedAt) {
    var category = new Category();
    category.id = id;
    category.name = name;
    category.note = note;
    category.active = active;
    category.createdBy = createdBy;
    category.createdAt = createdAt;
    category.updatedBy = updatedBy;
    category.updatedAt = updatedAt;
    return category;
  }

  // ── Behavior ──

  public void rename(String newName, String newNote) {
    requireActive();
    requireValidName(newName);
    this.name = newName.strip();
    this.note = newNote;
  }

  public void deactivate() {
    requireActive();
    this.active = false;
  }

  // ── Invariant enforcement ──

  private void requireActive() {
    if (!this.active) {
      throw new IllegalStateException("Category is not active: id=" + id);
    }
  }

  private static void requireValidName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name must not be blank");
    }
  }

  // ── Getters (no setters) ──

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getNote() {
    return note;
  }

  public boolean isActive() {
    return active;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Long getUpdatedBy() {
    return updatedBy;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  // ── Equality by identity ──

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Category that)) return false;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  // Private constructor — use factories
  private Category() {}
}
