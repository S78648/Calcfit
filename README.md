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

