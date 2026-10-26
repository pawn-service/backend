package com.spring.backend.catalog.application.dto;

/** Command to rename/update an existing category. */
public record UpdateCategoryCommand(Long id, String name, String note) {}
