# Ubiquitous language

One term = one meaning, used identically in code, docs and conversation. Add terms when they appear.

| Term | Context | Meaning | Code name |
|------|---------|---------|-----------|
| Customer | identity, ordering | A user with role CUSTOMER who buys products | `CustomerId` |
| Product | catalog | An item listed for sale, with price and stock | `Product` |
| Category | catalog | Grouping of products | `Category` |
| Cart | ordering | Customer's pending selection of products before ordering | `Cart` |
| Order | ordering | Confirmed purchase of products at fixed prices | `Order` |
| Order item | ordering | One product line in an order, with price snapshot | `OrderItem` |
| Payment | payment | An attempt to pay for an order via Stripe | `Payment` |
| Card | payment | A customer's saved payment card | `Card` |
| Reconciliation | payment | Job matching local payment status with Stripe | — |
