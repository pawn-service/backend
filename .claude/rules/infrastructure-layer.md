---
paths:
  - "src/main/java/**/infrastructure/**/*.java"
---
# Infrastructure layer (adapters out)

## Persistence

- `XxxJpaEntity` (JPA annotations, Lombok OK) is separate from the domain model.
- `XxxRepositoryAdapter implements domain.repository.XxxRepository`, delegates to `SpringDataXxxRepository extends JpaRepository`.
- Map with a dedicated `XxxPersistenceMapper` (domain ⇄ JPA). Use the aggregate's `reconstitute(...)` factory.
- Keep existing table/column names — no schema changes during migration unless explicitly requested.

## External clients

- Stripe, S3, OpenAI adapters implement `application.port.out.*` interfaces.
- Translate SDK exceptions/DTOs into application types; SDK classes must not leak upward.
- Config values via `@ConfigurationProperties` records; no hardcoded secrets or defaults for credentials.
