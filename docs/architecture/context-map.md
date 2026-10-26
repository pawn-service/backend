# Context map

Draft — refine as each context is migrated.

| Context | Responsibility | Core aggregates | Type |
|---------|----------------|-----------------|------|
| ordering | Cart and order lifecycle | `Cart`, `Order` | Core |
| catalog | Products, categories, stock | `Product`, `Category` | Core |
| payment | Checkout session, payment status, saved cards | `Payment`, `Card` | Supporting |
| identity | Users, roles, authentication tokens | `User` | Generic |
| assistant | AI chat about products | `Conversation` | Supporting |
| media | Image/file storage | — (technical) | Generic |
| reporting | Dashboard read models | — (query only) | Supporting |

## Relationships

```
catalog ──(product snapshot: id, name, price)──▶ ordering
ordering ──(OrderPlaced)──▶ payment
payment ──(PaymentSucceeded / PaymentFailed)──▶ ordering
ordering ──(OrderPaid)──▶ catalog        (decrease stock)
identity ──(customer id)──▶ ordering, payment
catalog, ordering ──▶ reporting          (read only)
payment ──ACL──▶ Stripe                  (anti-corruption layer)
assistant ──ACL──▶ OpenAI
media ──ACL──▶ S3
```

Rules: contexts share only IDs and events/application DTOs; never domain objects.
