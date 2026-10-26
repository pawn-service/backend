---
paths:
  - "src/main/java/**/presentation/**/*.java"
---
# Presentation layer (REST)

- Controllers are thin: map `XxxRequest` → command, call one use case, map result → `XxxResponse`.
- Request/response are `record`s with Bean Validation (`@NotNull`, `@Positive`…) for shape only; business validation stays in domain.
- Never expose domain models or JPA entities in responses.
- **Keep URL paths, HTTP methods, JSON field names and status codes identical to the legacy controller** — the frontend depends on them.
- Security annotations (`@PreAuthorize`) stay on controllers, as in legacy code.
- Errors are mapped centrally in `shared.infrastructure` exception handler (domain exception → HTTP status).
