# Restaurant Booking Service

This is a Spring Boot application for managing restaurant bookings.

## Features

- Manage Restaurants (CRUD)
- Manage Bookings (CRUD)
- H2 In-memory Database
- Swagger UI for API documentation

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle

### Running the application

To build and run the application, navigate to the project root directory and execute the following command:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

### API Documentation (Swagger UI)

Once the application is running, you can access the API documentation via Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### H2 Console

The H2 in-memory database console can be accessed at:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use the following credentials:
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User Name:** `sa`
- **Password:** (leave blank)