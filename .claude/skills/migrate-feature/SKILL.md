---
name: migrate-feature
description: Migrate one legacy feature (controller/service/repository/entity) into its bounded context using the strangler pattern, keeping the REST contract unchanged. Use when the user asks to refactor/migrate a feature to Clean Architecture or DDD.
---
# Migrate a legacy feature

Scope: **one use case at a time** (e.g. "place order"), not a whole context.

## 1. Analyse (no edits yet)

- Locate legacy code: controller → service → repository → entity → dto.
- List: REST endpoints (path, method, JSON), business rules found in service/controller, external calls, transactions.
- Identify the aggregate root, value objects and invariants. Propose names from the ubiquitous language.
- Present a short plan (table: legacy class → new class/layer) and **wait for user approval**.

## 2. Safety net

- Ensure an integration test covers the endpoint contract. If missing, write it against the legacy code first and run `./gradlew integrationtest`.

## 3. Build inside-out

1. Domain: aggregate + value objects + repository interface + unit tests (no Spring).
2. Application: use case + command/result + port/out interfaces + Mockito tests.
3. Infrastructure: `*JpaEntity` (same table), mapper, `*RepositoryAdapter`, client adapters.
4. Presentation: new controller with **identical** path/JSON; remove the legacy endpoint method.

## 4. Clean up & verify

- Delete legacy classes no longer referenced.
- `./gradlew check` must pass.
- Run the `architecture-reviewer` agent on changed files.
- Update the status table in `docs/architecture/README.md`; write an ADR if a non-obvious decision was made.

## Explain as you go

The user is learning. After each step, give 1–2 lines: which DDD/CA concept was applied and why.
