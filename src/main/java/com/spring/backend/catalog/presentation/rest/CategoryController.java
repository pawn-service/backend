package com.spring.backend.catalog.presentation.rest;

import com.spring.backend.catalog.application.dto.CreateCategoryCommand;
import com.spring.backend.catalog.application.dto.UpdateCategoryCommand;
import com.spring.backend.catalog.application.usecase.CategoryQueryService;
import com.spring.backend.catalog.application.usecase.CreateCategoryUseCase;
import com.spring.backend.catalog.application.usecase.DeactivateCategoryUseCase;
import com.spring.backend.catalog.application.usecase.UpdateCategoryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Category — replaces legacy controller/category/CategoryController.
 *
 * <p>Paths, HTTP methods, JSON field names and status codes are identical to legacy.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CreateCategoryUseCase createCategoryUseCase;
  private final UpdateCategoryUseCase updateCategoryUseCase;
  private final DeactivateCategoryUseCase deactivateCategoryUseCase;
  private final CategoryQueryService categoryQueryService;

  @PostMapping
  public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
    var command = new CreateCategoryCommand(request.name(), request.note());
    var result = createCategoryUseCase.execute(command);
    return CategoryResponse.from(result);
  }

  @GetMapping
  public List<CategoryResponse> getAllCategories() {
    return categoryQueryService.getAll().stream().map(CategoryResponse::from).toList();
  }

  @GetMapping("/search")
  public Page<CategoryResponse> searchCategories(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size) {
    return categoryQueryService.search(name, page, size).map(CategoryResponse::from);
  }

  @GetMapping("/{id}")
  public CategoryResponse getCategoryById(@PathVariable Long id) {
    return CategoryResponse.from(categoryQueryService.getById(id));
  }

  @PutMapping("/{id}")
  public CategoryResponse updateCategory(
      @PathVariable Long id, @RequestBody CategoryRequest request) {
    var command = new UpdateCategoryCommand(id, request.name(), request.note());
    var result = updateCategoryUseCase.execute(command);
    return CategoryResponse.from(result);
  }

  @DeleteMapping("/{id}")
  public void deleteCategory(@PathVariable Long id) {
    deactivateCategoryUseCase.execute(id);
  }
}
