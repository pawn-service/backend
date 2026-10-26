package com.spring.backend.catalog.presentation.rest;

import com.spring.backend.catalog.application.dto.CategoryDetailResult;
import com.spring.backend.catalog.application.dto.CategoryResult;
import com.spring.backend.catalog.application.port.out.CreatorLookupPort.CreatorInfo;
import java.time.Instant;

/**
 * Matches legacy CategoryResponseDto JSON exactly:
 *
 * <pre>
 * {
 *   "id": 1,
 *   "name": "Electronics",
 *   "note": "Tech",
 *   "isActive": true,
 *   "createdAt": "...",
 *   "createdBy": 10,
 *   "createdByUser": { "id": 10, "email": "...", "name": "...", ... },
 *   "updatedAt": "...",
 *   "updatedBy": 10
 * }
 * </pre>
 */
public record CategoryResponse(
    Long id,
    String name,
    String note,
    Boolean isActive,
    Instant createdAt,
    Long createdBy,
    CreatedByUser createdByUser,
    Instant updatedAt,
    Long updatedBy) {

  /** Matches legacy UserDto JSON structure used inside category responses. */
  public record CreatedByUser(
      Long id,
      String email,
      String name,
      String username,
      Integer age,
      String birthDate,
      String phone,
      String cardId,
      String address,
      String gender,
      String role,
      String avatar,
      Boolean isActive) {}

  // ── Mapping ──

  public static CategoryResponse from(CategoryDetailResult detail) {
    CategoryResult cat = detail.category();
    return new CategoryResponse(
        cat.id(),
        cat.name(),
        cat.note(),
        cat.active(),
        cat.createdAt(),
        cat.createdBy(),
        mapCreator(detail.createdByUser()),
        cat.updatedAt(),
        cat.updatedBy());
  }

  public static CategoryResponse from(CategoryResult result) {
    return new CategoryResponse(
        result.id(),
        result.name(),
        result.note(),
        result.active(),
        result.createdAt(),
        result.createdBy(),
        null,
        result.updatedAt(),
        result.updatedBy());
  }

  private static CreatedByUser mapCreator(CreatorInfo info) {
    if (info == null) return null;
    return new CreatedByUser(
        info.id(),
        info.email(),
        info.name(),
        info.username(),
        info.age(),
        info.birthDate(),
        info.phone(),
        info.cardId(),
        info.address(),
        info.gender(),
        info.role(),
        info.avatar(),
        null);
  }
}
