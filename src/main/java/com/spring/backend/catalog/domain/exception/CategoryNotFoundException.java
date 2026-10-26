package com.spring.backend.catalog.domain.exception;

import com.spring.backend.shared.domain.DomainException;

public class CategoryNotFoundException extends DomainException {

  public CategoryNotFoundException(Long id) {
    super("Category not found: id=" + id);
  }
}
