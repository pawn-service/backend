package com.spring.backend.catalog.application.port.out;

import com.spring.backend.catalog.application.dto.CategoryResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Read-only port for category queries. Separate from the domain repository because queries don't
 * need to go through the aggregate — they return flat DTOs directly (CQRS-lite).
 *
 * <p>Spring {@link Page} and {@link Pageable} are allowed here because this is an application port
 * for reads, not a domain interface.
 */
public interface CategoryQueryPort {

  Optional<CategoryResult> findById(Long id);

  List<CategoryResult> findAll();

  Page<CategoryResult> searchByName(String name, Pageable pageable);
}
