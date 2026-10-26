# Architecture

Target: **Clean Architecture + DDD**, package-by-bounded-context. Decision: [ADR-0001](../adr/0001-clean-architecture-ddd.md).

## Layers

```
            ┌──────────────────┐
            │   presentation   │  REST controllers, Request/Response
            └────────┬─────────┘
                     ▼
            ┌──────────────────┐
            │   application    │  use cases, ports (out), commands/results
            └────────┬─────────┘
                     ▼
            ┌──────────────────┐
            │      domain      │  aggregates, value objects, events, repo interfaces
            └──────────────────┘
                     ▲
            ┌────────┴─────────┐
            │  infrastructure  │  JPA, Redis, Stripe, S3, OpenAI adapters
            └──────────────────┘
```

Arrows = compile-time dependency. Detailed rules: [`.claude/rules/`](../../.claude/rules/).

## Related docs

- [Context map](context-map.md) — bounded contexts and how they relate
- [Ubiquitous language](ubiquitous-language.md) — shared vocabulary
- [ADRs](../adr/) — decisions and their reasons

## Migration status

Legacy = current layered packages (`controller/service/repository/entity/dto/...`).

| Context | Legacy source | Status |
|---------|---------------|--------|
| identity | `UserService`, `AuthService`, `UserEntity`, `TokenEntity`, `JwtTokenHelper` | ⬜ legacy |
| catalog | `ProductService`, ~~`CategoryService*`~~, `InventoryService`, `ProductEntity`, `CategoryEntity`, `ImageEntity` | 🟨 Category migrated; Product, Inventory, Image still legacy |
| ordering | `CartService`, `OrderService`, `Cart*Entity`, `Order*Entity` | ⬜ legacy |
| payment | `PaymentGatewayService`, `CardService`, `StripeAdapter`, `PaymentEntity`, `CardEntity`, `ReconciliationJobService` | ⬜ legacy |
| assistant | `AgentService`, `AgentContextService`, `ChatHistoryService`, `service/chat`, `OpenAiClient` | ⬜ legacy |
| media | `FileUploadService`, `S3Adapter` | ⬜ legacy |
| reporting | `DashboardService`, `ProductStatistic` | ⬜ legacy |

Legend: ⬜ legacy · 🟨 in progress · ✅ migrated
