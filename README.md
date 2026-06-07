# Calorie Management (Calcfit)

A Spring Boot application to manage calorie intake on a daily basis. The backend includes **JWT-based authentication**, **Spring Security**, **JPA/Hibernate**, and **Flyway** database migrations.

## Tech stack

- **Java**: 21
- **Spring Boot**: 4.x
- **Web**: Spring MVC (Tomcat)
- **Security**: Spring Security + JWT (JJWT)
- **Persistence**: Spring Data JPA (Hibernate)
- **Database**: PostgreSQL
- **Migrations**: Flyway
- **Build**: Gradle

## Prerequisites

- **JDK 21**
- **PostgreSQL** running locally (for `dev` profile)
- (Optional) An API client like Postman

## Configuration profiles

This project uses Spring profiles for database configuration:

- **`dev`**: local PostgreSQL connection (see `src/main/resources/application-dev.yaml`)
- **`prod`**: database settings via environment variables (see `src/main/resources/application-prod.yaml`)

The app is configured to default to **`dev`** if no profile is set.

## Running locally (dev)

1. Start PostgreSQL and create a database (example name used in dev config: `calorie_db`).
2. Run the app:

```bash
./gradlew bootRun
```

The server starts on `http://localhost:8060` by default.

## Running with a specific profile

### Dev

```bash
./gradlew bootRun -Dspring.profiles.active=dev
```

### Prod

Set the following environment variables:

- `DB_URL` (example: `jdbc:postgresql://host:5432/dbname`)
- `DB_USERNAME`
- `DB_PASSWORD`

Then run:

```bash
./gradlew bootRun -Dspring.profiles.active=prod
```

## Database migrations (Flyway)

Flyway is enabled and will run migrations automatically on startup.

Migration files are located under:

- `src/main/resources/db/migration`

If startup fails due to migration/validation, confirm:

- the database exists and is reachable
- the configured user has permissions
- migrations have been applied consistently across environments

## Authentication overview

Authentication is JWT-based:

- **Access token**: short-lived
- **Refresh token**: longer-lived, used to obtain a new access token

Typical flow:

1. Sign up / log in to obtain tokens
2. Call protected APIs with `Authorization: Bearer <access_token>`
3. Refresh access token when expired

## Notes / Roadmap

- **RBAC**: pending (role-based access control)
- **OAuth**: pending (external login / identity provider integration)

## Troubleshooting

- **App fails to start with “Failed to configure a DataSource”**:
  - Ensure PostgreSQL is running and reachable
  - Ensure `dev` profile is active (or `prod` env vars are set)
  - Confirm the JDBC URL/credentials are correct

## Swagger API Documentation

This project uses Swagger/OpenAPI for interactive API documentation and testing.

### Swagger UI

After running the application, open:

```text
http://localhost:8060/swagger-ui/index.html
```

### OpenAPI Docs

```text
http://localhost:8060/v3/api-docs
```

---

## JWT Authentication in Swagger

Protected APIs require JWT authentication.

### Steps

1. Login using:

```http
POST /auth/login
```

2. Copy the JWT token from the response.

3. Click the **Authorize** button in Swagger UI.

4. Enter the token in this format:

```text
Bearer your-jwt-token
```

5. You can now access secured endpoints directly from Swagger UI.

---

## Features

* Interactive API documentation
* JWT authentication support
* Request/response schema visualization
* Secure endpoint testing
* OpenAPI 3 integration

---

## Security Notes

The following endpoints are publicly accessible:

```text
/auth/**
/swagger-ui/**
/v3/api-docs/**
```

All other endpoints require authentication.

## Testing

This project uses **JUnit 5** and **Mockito** for unit testing.

### Testing Strategy

The application follows two types of unit testing:

#### 1. JUnit Tests

Used for testing pure business logic that has no external dependencies.

Examples:

* Calorie calculations
* Protein calculations
* Macronutrient calculations
* Utility methods
* Validation logic

Example:

```java
//assertEquals(
//    BigDecimal.valueOf(130.00).setScale(2),
//    target.getTargetProteinGrams()
//);
```

---

#### 2. Mockito Tests

Used when a service depends on:

* Repositories
* External APIs
* Redis
* Email services
* Other Spring services

Mockito allows testing business logic without requiring a real database.

Example:

```java
//when(userRepository.findById(userId))
//        .thenReturn(Optional.of(user));
```

This tells Mockito:

> When the service calls `userRepository.findById()`, return the provided user.

---

### Current Test Coverage

#### TargetCalculationService

* Calorie calculation
* Protein calculation
* Fat calculation
* Carbohydrate calculation
* Water intake calculation
* Fiber calculation

#### UserProfileService

* Create new profile
* Update existing profile
* Get user dashboard
* Recalculate targets
* Resource not found scenarios

---

### Running Tests

Run all tests:

```bash
./gradlew test
```

Run a specific test class:

```bash
./gradlew test --tests "com.calorie.management.TargetCalculationServiceTest"
```

---

### Learning Notes

A useful rule:

* Use **JUnit** when testing pure logic.
* Use **Mockito** when testing code that depends on repositories or external services.

Mockito does not access the database. It only simulates responses, making tests fast, isolated, and deterministic.

Docker Setup
Prerequisites

Install:

Docker Desktop
Git
Java 21 (for local development)

Verify Docker installation:

docker --version
docker compose version
Build and Run Application
Start Application
docker compose up --build

Run in background:

docker compose up --build -d
Stop Containers
docker compose down
Rebuild After Code Changes
docker compose down
docker compose up --build -d
Force Clean Rebuild
docker compose down
docker compose build --no-cache
docker compose up -d
View Running Containers
docker ps

Example:

CONTAINER ID   IMAGE
abc123         calorie_management-app
xyz456         postgres:16-alpine
View Application Logs
docker logs -f calorie_management-app-1
Access PostgreSQL Container

Find container name:

docker ps

Example:

calorie_management-db-1

Connect:

docker exec -it calorie_management-db-1 psql -U postgres -d calorie_db

Useful commands:

\dt
SELECT * FROM users;
SELECT * FROM user_profiles;

Exit:

\q
Environment Variables

Application uses environment variables in production:

JWT_ISSUER=calcfit
JWT_SECRET=<your-secret>
JWT_ACCESS_TTL_SECONDS=3600
JWT_REFRESH_TTL_SECONDS=604800
Docker Architecture
┌─────────────────────────┐
│ Spring Boot Application │
│ Port 8060              │
└──────────┬──────────────┘
│
▼
┌─────────────────────────┐
│ PostgreSQL Database     │
│ Port 5432              │
└─────────────────────────┘

