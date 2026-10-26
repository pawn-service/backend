package com.spring.backend.catalog.application.dto;

import java.time.Instant;

/**
 * Result returned by category use cases. Shared across create, update, and query operations.
 *
 * <p>This is what leaves the application layer — never the domain model itself.
 */
public record CategoryResult(
    Long id,
    String name,
    String note,
    boolean active,
    Long createdBy,
    Instant createdAt,
    Long updatedBy,
    Instant updatedAt) {}
