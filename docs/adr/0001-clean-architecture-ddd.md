# ADR-0001: Clean Architecture + DDD, package-by-bounded-context

- Status: accepted
- Date: 2026-10-26

## Context

The codebase is layered by technical type (`controller/service/repository/entity/dto`). Business rules live in services, JPA entities are passed across layers, and features are hard to change in isolation. The project is also a learning vehicle for Clean Architecture and DDD.

## Decision

- Organise code by bounded context (`com.spring.backend.<context>`), each with `domain / application / infrastructure / presentation`.
- Domain is framework-free; JPA entities are separate from domain models.
- Pragmatic exception: application layer may use Spring `@Service` and `@Transactional`.
- Single Gradle module; boundaries enforced by convention + `architecture-reviewer` agent (ArchUnit may be added later).
- Migrate incrementally (strangler pattern), one use case at a time, keeping the REST and Stripe webhook contracts unchanged.

## Consequences

- ➕ Business rules testable without Spring; clear place for every class.
- ➕ Contexts can later become separate modules/services.
- ➖ More classes and mapping code (domain ⇄ JPA ⇄ DTO).
- ➖ Legacy and new structure coexist during migration.
