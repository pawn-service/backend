---
paths:
  - "src/main/java/**/domain/**/*.java"
---
# Domain layer (DDD tactical patterns)

## Purity

- No Spring, JPA, Jackson, Lombok `@Data`/`@Setter`, no `javax/jakarta.*`. JDK + `shared.domain` only.
- No public setters. State changes only via intention-revealing methods: `order.markPaid(paymentId)`, not `setStatus(PAID)`.

## Building blocks

- **Aggregate root**: consistency boundary; only the root is loaded/saved by a repository. Children are modified only through the root.
- **Entity**: has identity (`OrderId`), equality by ID.
- **Value object**: immutable `record`, equality by value, validates itself in the compact constructor (`Money`, `Quantity`, `Email`).
- **Domain event**: past-tense record (`OrderPlaced`), raised by the aggregate, published by the use case after save.
- **Domain service**: stateless, only when logic does not belong to a single aggregate.
- **Repository interface**: lives here, speaks in aggregates (`Optional<Order> findById(OrderId)`, `void save(Order)`), no paging/Spring types.

## Invariants

- Enforce invariants inside the aggregate; it must never be in an invalid state.
- Create via static factories (`Order.place(...)`) that validate; use `reconstitute(...)` for rehydrating from persistence.
- Throw domain exceptions (extend a `shared.domain.DomainException`), never HTTP or Spring exceptions.

## Keep aggregates small

- One transaction modifies one aggregate. Cross-aggregate consistency → domain events (eventual consistency).
