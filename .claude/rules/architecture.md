---
paths:
  - "src/main/java/**/*.java"
---
# Clean Architecture — package layout & dependency rule

## Layout per bounded context

```
com.spring.backend.<context>/
  domain/
    model/          aggregates, entities, value objects, domain events
    repository/     repository interfaces (one per aggregate root)
    service/        domain services (logic spanning aggregates)
    exception/      domain exceptions
  application/
    usecase/        one class per use case: PlaceOrderUseCase#execute(cmd)
    port/out/       interfaces to external systems (PaymentGateway, FileStorage)
    dto/            commands, queries, results (records)
  infrastructure/
    persistence/    *JpaEntity, Spring Data repos, *RepositoryAdapter, mappers
    client/         Stripe/S3/OpenAI adapters implementing port/out
    config/         context-specific Spring config
  presentation/
    rest/           controllers, *Request/*Response records, web mappers
```

`com.spring.backend.shared/` holds the shared kernel (`shared.domain`: AggregateRoot, DomainEvent, Money…)
and cross-cutting infra (`shared.infrastructure`: security, exception handler, Redis, JobRunr config).

## Dependency rule (must never break)

| Layer | May import |
|-------|-----------|
| domain | JDK, `shared.domain` |
| application | domain, `shared.domain`, Spring `@Service`/`@Transactional` only |
| infrastructure | application, domain, any framework |
| presentation | application (use cases + dto). **Never** domain models or infrastructure |

- Contexts talk to each other through the other context's `application` API or domain events. Never import another context's `domain` or `infrastructure`.
- Reference other aggregates **by ID**, not by object.
- JPA entities (`*JpaEntity`) never leave `infrastructure`.

## When unsure where code goes

- Business invariant / rule → domain model.
- Orchestration (load → call domain → save → publish) → use case.
- Talks to DB / HTTP / SDK → infrastructure.
- HTTP mapping / validation of request shape → presentation.
