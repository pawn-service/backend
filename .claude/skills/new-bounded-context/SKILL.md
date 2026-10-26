---
name: new-bounded-context
description: Scaffold a new bounded context package (domain/application/infrastructure/presentation) following this repo's Clean Architecture + DDD layout. Use when the user asks to create a new context/module.
---
# New bounded context

Input: context name (lowercase, singular noun from the context map, e.g. `ordering`).

1. Check [docs/architecture/context-map.md](../../../docs/architecture/context-map.md). If the context is not there, ask the user to confirm its responsibility first, then add it.
2. Create packages under `src/main/java/com/spring/backend/<context>/`:
   `domain/{model,repository,service,exception}`, `application/{usecase,port/out,dto}`,
   `infrastructure/{persistence,client,config}`, `presentation/rest`.
   Add a `package-info.java` only in the 4 layer roots, with one line describing the layer.
3. Mirror test packages in `src/unittest/java/...` and `src/integrationtest/java/...`.
4. Add new domain terms to [docs/architecture/ubiquitous-language.md](../../../docs/architecture/ubiquitous-language.md).
5. Add a row to the migration status table in [docs/architecture/README.md](../../../docs/architecture/README.md).
6. Run `./gradlew compileJava`.

Do not create empty placeholder classes. Report: created packages + next suggested aggregate.
