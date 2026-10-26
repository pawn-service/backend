---
paths:
  - "src/unittest/**/*.java"
  - "src/integrationtest/**/*.java"
---
# Testing

Source sets: `src/unittest` (`./gradlew unittest`), `src/integrationtest` (`./gradlew integrationtest`). Test classes end with `UT` / `IT`.

| Layer | Test style |
|-------|-----------|
| domain | Plain JUnit 5 + AssertJ. No Spring, no mocks. Test invariants and state transitions. |
| application | JUnit 5 + Mockito for repositories/ports. Verify orchestration and events. |
| infrastructure | `@DataJpaTest` (H2) for repository adapters; WireMock for Stripe/S3/OpenAI. |
| presentation | `@WebMvcTest` / MockMvc; assert JSON contract matches legacy. |

- Name: `method_scenario_expected` + `@DisplayName`.
- Before migrating a feature, make sure an IT covers its REST contract; it must stay green after migration.
- JaCoCo gate is 70% (`./gradlew check`).
