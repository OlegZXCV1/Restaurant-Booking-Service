# Repository Guidelines

## Project Structure & Module Organization
- Core code lives in `src/main/java/com/example/restaurantbookingservice` with domain layers split into `controller`, `service`, `repository`, `model`, `dto`, and `mapper` (MapStruct).
- Security and JWT utilities sit under `config` and `security`.
- Properties: `src/main/resources/application.properties` selects profiles; `application-dev.properties` uses H2 on port 8081, `application-prod.properties` targets Postgres.
- Tests are under `src/test/java/com/example/restaurantbookingservice`, separated by `controller` and `service` packages.

## Build, Test, and Development Commands
- `./mvnw clean verify` — full build with tests and annotation processing (Java 17).
- `./mvnw test` — unit/integration tests only.
- `./mvnw spring-boot:run` — run locally with the active profile (defaults to `dev` on port 8081).
- `docker-compose up -d postgres` — start local Postgres matching `application-prod.properties`.

## Coding Style & Naming Conventions
- Java 17, 4-space indentation, prefer Lombok for boilerplate (`@Getter`, `@Setter`, `@Builder`) and MapStruct mappers for DTO translation.
- Packages follow `com.example.restaurantbookingservice` — keep new code in this root and align class names with roles (`*Controller`, `*Service`, `*Repository`, `*Dto`, `*Mapper`).
- Validate inputs with `jakarta.validation` annotations; surface errors via controller advice patterns already present.
- Favor constructor injection; keep controllers slim and delegate to services.

## Testing Guidelines
- JUnit 5 and Spring Boot test starters are in place. Use `@SpringBootTest` for wiring-sensitive paths and `@WebMvcTest`/`@DataJpaTest` where possible to keep scope tight.
- Mirror package names between `src/main` and `src/test` for discoverability; test class names should end with `Test` or `IntegrationTest`.
- Run `./mvnw test` before pushing; aim to cover new business logic branches and security paths.

## Commit & Pull Request Guidelines
- Commit messages in this repo favor short, imperative summaries (e.g., `add tests`, `fix controller validation`). Follow that style and keep changes logically grouped.
- PRs should describe intent, main changes, and any follow-up work; link issues when available and note new endpoints or config toggles (ports, profiles, JWT settings).
- Include screenshots or sample requests when altering API behavior or Swagger docs URLs.

## Security & Configuration Tips
- Never commit real secrets; override `jwt.secret` and database credentials via environment variables or a profile-specific properties file ignored by Git.
- Keep `application.properties` minimal; profile-specific files should own environment differences (ports, datasource URLs).
- When enabling Postgres, ensure the compose service name/port matches `application-prod.properties` or adjust `SPRING_DATASOURCE_URL` accordingly.
