---
paths:
  - "src/**/*.java"
---
# Java style

- Formatting is enforced by Spotless (google-java-format); `./gradlew compileJava` applies it. Don't hand-format.
- `record` for value objects, DTOs, commands, results.
- `final` fields by default; constructor injection (`@RequiredArgsConstructor` OK outside domain).
- `Optional` only as a return type of finders; never `get()` without a check — use `orElseThrow`.
- Unchecked, specific exceptions with context in the message (`OrderNotFoundException(orderId)`).
- No broad `catch (Exception e)` except at top-level handlers.
- Naming: `*UseCase`, `*Command`, `*Result`, `*JpaEntity`, `*RepositoryAdapter`, `*Request`, `*Response`.
- Never log passwords, tokens, card numbers or PII.
