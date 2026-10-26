package com.spring.backend.catalog.application.dto;

/** Command to create a new category. Immutable input to the use case. */
public record CreateCategoryCommand(String name, String note) {}
