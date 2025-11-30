# Restaurant Booking Service

This is a Spring Boot application that exposes a REST API for restaurants, restaurant tables, time slots, and bookings on top of JWT-secured endpoints.

## Highlights

- Modular `controller` / `service` / `repository` layers under `com.example.restaurantbookingservice`
- Security handled through JWT utilities and custom `UserDetailsServiceImpl`
- H2 used in `dev`/`test` profiles, Postgres target in `prod` with docker compose
- Swagger / OpenAPI UI and SpringDoc configuration under `config`

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven

### Getting started

1. Build and test the project locally:

   ```bash
   ./mvnw clean verify
   ```

2. Launch the app with the default `dev` profile (H2 + port 8081):

   ```bash
   ./mvnw spring-boot:run
   ```

3. For integration tests that hit a more complete context (Postgres, async flows):

   ```bash
   ./mvnw test
   ```

4. When Postgres is required, start the container that matches `application-prod.properties`:

   ```bash
   docker-compose up -d postgres
   ```

   Then override the profile and JDBC URL via `SPRING_PROFILES_ACTIVE=prod` and `SPRING_DATASOURCE_URL`.

### Environment / profiles

- `application.properties` punts to `dev` (H2 on 8081). `application-dev.properties` also targets H2, `application-test.properties` mirrors the test setup, and `application-prod.properties` points at Postgres (update credentials via env vars).
- Spring Security profile `test-security` is used by controller slices; drop-down `@Profile` annotations in `config`/`security` toggle JWT vs. mock security in tests.

### APIs & tooling

- Swagger/OpenAPI UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) (enabled when running any web profile).
- H2 console (dev/test): [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  - JDBC URL: `jdbc:h2:mem:testdb`
  - User: `sa`
  - Password: (blank)

### Hexagonal/Clean Architecture notes

- Controllers live under `controller` and accept/return DTOs; services encapsulate business rules and rely on repositories (`adapter` layer) for persistence.
- Security adapters (`JwtTokenProvider`, filters) live under `security` and `config` while DTO/mappers are contained in their own packages for clear responsibility.

### Testing

- Unit/integration tests live under `src/test/java/com/example/restaurantbookingservice` with matching packages; use `./mvnw test` before pushing.
- Controller slices run with `@SpringBootTest` + `test-security` profile, so make sure no duplicate `UserDetailsService` beans exist (only `security.services.UserDetailsServiceImpl` should remain).
