---
paths:
  - "src/main/java/**/application/**/*.java"
---
# Application layer (use cases)

- One use case = one class = one public method: `XxxUseCase#execute(XxxCommand) -> XxxResult`.
- Flow: validate input → load aggregate(s) via repository → call domain behavior → save → publish domain events.
- No business rules here — if you write `if (order.getStatus() == ...)`, move it into the aggregate.
- Transaction boundary lives here (`@Transactional` on `execute`).
- Commands/queries/results are `record`s in `application/dto`. Never return domain objects or JPA entities to callers.
- External systems are accessed via interfaces in `application/port/out`; implementations live in `infrastructure`.
- Read-only screens (dashboard, listings) may use a dedicated query service that bypasses aggregates (CQRS-lite).
