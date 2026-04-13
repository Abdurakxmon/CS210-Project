# CS210 Car Rental System

This project is focused on authentication for now. It uses JavaFX for the UI, MySQL for storage, and Ebean ORM for persistence and migrations.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL Server 8.0+

## Configuration

Set your database connection with JVM properties or environment variables:

- `db.url` or `CAR_RENTAL_DB_URL`
- `db.user` or `CAR_RENTAL_DB_USER`
- `db.password` or `CAR_RENTAL_DB_PASSWORD`

Defaults:

- URL: `jdbc:mysql://localhost:3306/car_rental_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- User: `root`
- Password: empty string

## How to Run

Run the JavaFX app:

```powershell
mvn clean javafx:run -Ddb.url="jdbc:mysql://localhost:3306/car_rental_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" -Ddb.user="root" -Ddb.password=""
```

Run database migrations only:

```powershell
mvn exec:java@run-migrations -Ddb.url="jdbc:mysql://localhost:3306/car_rental_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" -Ddb.user="root" -Ddb.password=""
```

## Current Features
- Login with an existing account from the `accounts` table
- Register a new member account
- PBKDF2 password hashing
- Ebean-backed MySQL migration for the authentication schema
