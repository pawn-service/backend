# Plan: Migrate Category sang catalog bounded context

## Phân tích legacy

### REST contract (PHẢI giữ nguyên)
| Method | Path | Auth | Request body | Response |
|--------|------|------|-------------|----------|
| POST | /api/categories | ✅ | `{name, note}` | `{id, name, note, isActive, createdAt, createdBy, createdByUser, updatedAt, updatedBy}` |
| GET | /api/categories | ❌ | — | `[{...same}]` |
| GET | /api/categories/search?name=&page=&size= | ❌ | — | Spring `Page<{...same}>` |
| GET | /api/categories/{id} | ❌ | — | `{...same}` |
| PUT | /api/categories/{id} | ✅ | `{name, note}` | `{...same}` |
| DELETE | /api/categories/{id} | ✅ | — | void (200) |

### Business rules hiện tại
1. Create: tên + note, mặc định `isActive = true`, `createdBy` do JPA auditing.
2. Update: chỉ update category đang active. Đổi name + note.
3. Delete: soft-delete (isActive = false), chỉ với category đang active.
4. GetById: trả cả category inactive (khác với update/delete — **giữ nguyên hành vi này**).
5. GetAll + Search: trả cả inactive, kèm `createdByUser` (lấy từ UserRepository).

### Hành vi lạ (giữ nguyên, không sửa)
- `getById` không lọc `isActive` → trả cả inactive.
- Không tìm thấy → `RuntimeException` → có thể trả 500 thay vì 404.
- `create` trả về `createdByUser = null`.

### Cross-context dependency
- `CategoryEntity` được `ProductEntity` dùng qua `@ManyToOne`. Vẫn giữ nguyên bảng DB.
- `ProductService` gọi `CategoryRepository.findByIdAndIsActive()` — sẽ đổi sang gọi use case hoặc query service của catalog khi migrate Product.

---

## Các bước thực hiện

### Bước 1: Shared kernel + Domain model
**File tạo:**
- `shared/domain/DomainException.java`
- `catalog/domain/model/Category.java` (aggregate root)
- `catalog/domain/exception/CategoryNotFoundException.java`
- Unit test: `src/unittest/.../catalog/domain/model/CategoryTest.java`

**Khái niệm:** Aggregate root, invariant, intention-revealing method, domain exception.

### Bước 2: Domain repository interface
**File tạo:**
- `catalog/domain/repository/CategoryRepository.java` (interface, nói ngôn ngữ domain)

**Khái niệm:** Port (dependency inversion), domain repository khác Spring Data repository.

### Bước 3: Infrastructure — persistence adapter
**File tạo:**
- `catalog/infrastructure/persistence/CategoryJpaEntity.java` (giữ bảng `categories` cũ)
- `catalog/infrastructure/persistence/SpringDataCategoryRepository.java` (extends JpaRepository)
- `catalog/infrastructure/persistence/CategoryPersistenceMapper.java`
- `catalog/infrastructure/persistence/CategoryRepositoryAdapter.java` (implements domain repo)

**Khái niệm:** Adapter pattern, tách domain model khỏi JPA entity, mapper.

### Bước 4: Application — CreateCategory use case
**File tạo:**
- `catalog/application/dto/CreateCategoryCommand.java`
- `catalog/application/dto/CategoryResult.java`
- `catalog/application/usecase/CreateCategoryUseCase.java`
- Unit test: `src/unittest/.../catalog/application/usecase/CreateCategoryUseCaseTest.java`

**Khái niệm:** Use case orchestration, command/result pattern.

### Bước 5: Application — UpdateCategory + DeactivateCategory use cases
- `UpdateCategoryCommand`, `UpdateCategoryUseCase`
- `DeactivateCategoryUseCase`
- Unit tests

### Bước 6: Application — Query service (CQRS-lite)
- `catalog/application/port/out/CreatorLookupPort.java` (lấy tên người tạo)
- `catalog/application/usecase/CategoryQueryService.java` (getById, getAll, search)
- `catalog/infrastructure/client/CreatorLookupAdapter.java`

**Khái niệm:** CQRS-lite (query không cần qua aggregate), port/adapter cho cross-context.

### Bước 7: Presentation — controller mới
- `catalog/presentation/rest/CategoryController.java`
- `catalog/presentation/rest/CategoryRequest.java`
- `catalog/presentation/rest/CategoryResponse.java`
- Xóa `controller/category/CategoryController.java` cũ

### Bước 8: Dọn dẹp
- Xóa legacy: `CategoryServiceImpl`, `CategoryService`, `CategoryMapper`.
- Giữ `entity/CategoryEntity` cũ (ProductEntity vẫn dùng — sẽ xóa khi migrate Product).
- `./gradlew check` phải xanh.
- Cập nhật migration status table.

---

## Quy ước đặt tên file

| Layer | Naming |
|-------|--------|
| domain model | `Category` (không suffix) |
| domain repo | `CategoryRepository` (interface) |
| domain exception | `CategoryNotFoundException` |
| application | `CreateCategoryUseCase`, `CreateCategoryCommand`, `CategoryResult` |
| infra persistence | `CategoryJpaEntity`, `SpringDataCategoryRepository`, `CategoryRepositoryAdapter`, `CategoryPersistenceMapper` |
| infra client | `CreatorLookupAdapter` |
| presentation | `CategoryController`, `CategoryRequest`, `CategoryResponse` |
