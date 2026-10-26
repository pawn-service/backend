# Pawn Service Backend

Spring Boot 3.5 / Java 21 backend for a pawn marketplace: catalog, cart, order, Stripe payment, OpenAI chat.
Stack: MySQL (JPA), Redis, S3, JobRunr, Spring Security + JWT, Spring AI.

**Goal of this repo:** learning project — refactor a layered codebase into **Clean Architecture + DDD**.

## Communication

- Reply in Vietnamese. Short, direct, no filler. Lead with the answer.
- When explaining a CA/DDD concept: 1–2 sentences, then tie it to code in this repo.
- Point out the trade-off when a pragmatic choice deviates from "textbook" DDD.

## Commands

```bash
./gradlew compileJava        # also runs spotlessApply (google-java-format)
./gradlew unittest           # src/unittest
./gradlew integrationtest    # src/integrationtest (H2, WireMock, embedded Redis)
./gradlew check              # spotless + all tests + JaCoCo >= 70%
./gradlew bootRun
```

## Architecture

Target: package-by-bounded-context, each context split into 4 layers.
Full description: [docs/architecture/README.md](docs/architecture/README.md).

```
com.spring.backend.<context>/{domain, application, infrastructure, presentation}
com.spring.backend.shared/   # shared kernel + cross-cutting infra
```

**Dependency rule:** `presentation → application → domain ← infrastructure`. Domain imports nothing outside the JDK.

Layer rules load automatically from `.claude/rules/` when you touch matching files.

### Migration status (strangler pattern)

- Legacy packages (`controller`, `service`, `repository`, `entity`, `dto`, `adapter`, `helper`, `utils`, `job`, `enums`) still hold most code.
- **Do not add new code to legacy packages.** New features go into a bounded context.
- Migrate one feature at a time with the `migrate-feature` skill; update the status table in `docs/architecture/README.md`.
- Keep REST paths, JSON contracts and the Stripe webhook contract unchanged while migrating.

## Project layout for AI files

| Path | Purpose |
|------|---------|
| `CLAUDE.md` | This file — always loaded, keep it short |
| `.claude/rules/*.md` | Path-scoped rules (`paths:` frontmatter) — one topic per file |
| `.claude/skills/<name>/SKILL.md` | Repeatable workflows (scaffold, migrate) |
| `.claude/agents/*.md` | Subagents (e.g. architecture review) |
| `docs/architecture/` | Architecture, context map, ubiquitous language |
| `docs/adr/` | Architecture Decision Records — copy `0000-template.md` |

Adding a new rule/skill/agent: one concern per file, kebab-case name, register it in the table above only if it is a new *kind* of file.

## Constraints

- Keep frontend API and Stripe webhook compatibility.
- No secrets in code or config defaults; never read or print `.env`.
- Do not commit or push unless asked.
