# Car Rental API

Spring Boot REST API skeleton for a Car Rental System, generated from the provided system design PDF.

This project already includes:

- Spring Boot project structure
- MySQL configuration
- JPA entities based on the class diagram
- repository layer
- controller and service skeletons
- Docker Compose file for MySQL

This project does **not** include business logic yet.

Right now, the application is mainly useful for:

- reviewing the project structure
- starting the Spring Boot app
- creating the database schema from JPA entities
- testing the placeholder REST routes

## Current Status

What works now:

- the project starts as a Spring Boot application
- MySQL connection is configured
- Hibernate/JPA can create and update tables automatically
- REST endpoints exist
- request DTOs and validation annotations exist

What is still not implemented:

- service logic
- create/read/update/delete behavior
- reservation workflow
- payment processing
- notification sending
- authentication and authorization
- custom exception handling

Most controller endpoints currently return:

- `501 Not Implemented`

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL 8
- Lombok
- Docker Compose

## Project Structure

```text
src/main/java/com/carrental/api
|-- controller
|-- dto/request
|-- entity
|   |-- enums
|-- repository
|-- service
|   |-- impl
`-- CarRentalApiApplication.java
```

Important files:

- `pom.xml` - Maven dependencies
- `compose.yaml` - local MySQL container
- `src/main/resources/application.properties` - database and Spring config

## Prerequisites

Install these before running the project:

- Java 21
- Maven 3.9+
- Docker Desktop or Docker Engine

## Database Setup

### Option 1: Start MySQL with Docker Compose

From the project root:

```bash
docker compose up -d
```

This starts MySQL using the values from `compose.yaml`.

Default database settings:

- database: `car_rental_db`
- username: `car_rental_user`
- password: `car_rental_password`
- port: `3306`

To stop MySQL:

```bash
docker compose down
```

To stop MySQL and remove database volume:

```bash
docker compose down -v
```

### Option 2: Use your own MySQL instance

If you already have MySQL installed locally, create a database and pass connection settings through environment variables.

Supported environment variables:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`

Example:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=car_rental_db
export DB_USERNAME=car_rental_user
export DB_PASSWORD=car_rental_password
export SERVER_PORT=8080
```

## Run the Application

From the project root:

```bash
mvn spring-boot:run
```

Or build first:

```bash
mvn clean package
java -jar target/car-rental-api-0.0.1-SNAPSHOT.jar
```

The application will start on:

- `http://localhost:8080`

## What Happens on Startup

Because this project uses:

- `spring.jpa.hibernate.ddl-auto=update`

Spring Boot will try to:

- connect to MySQL
- create missing tables
- update the schema from the JPA entities

This means the database structure is generated automatically from the entity classes.

## API Endpoints Available Right Now

These routes already exist in the codebase:

### Locations

- `POST /api/v1/locations`
- `GET /api/v1/locations`
- `GET /api/v1/locations/{id}`

### Members

- `POST /api/v1/members`
- `GET /api/v1/members`
- `GET /api/v1/members/{id}`

### Vehicles

- `POST /api/v1/vehicles`
- `GET /api/v1/vehicles`
- `GET /api/v1/vehicles/{id}`
- `POST /api/v1/vehicles/logs`

### Reservations

- `POST /api/v1/reservations`
- `GET /api/v1/reservations`
- `GET /api/v1/reservations/{id}`

### Payments

- `POST /api/v1/payments`
- `GET /api/v1/payments`
- `GET /api/v1/payments/{id}`

### Notifications

- `GET /api/v1/notifications/reservation/{reservationId}`

## Request Payload Shapes

These are the request formats currently defined by the DTOs.

### Create Location

```json
{
  "name": "Airport Branch",
  "address": {
    "streetAddress": "123 Main St",
    "city": "Tashkent",
    "state": "Tashkent",
    "zipcode": "100000",
    "country": "Uzbekistan"
  }
}
```

### Create Member

```json
{
  "person": {
    "name": "John Doe",
    "address": {
      "streetAddress": "123 Main St",
      "city": "Tashkent",
      "state": "Tashkent",
      "zipcode": "100000",
      "country": "Uzbekistan"
    },
    "email": "john@example.com",
    "phone": "+998901234567"
  },
  "password": "secret123",
  "driverLicenseNumber": "DL-123456",
  "driverLicenseExpiry": "2028-12-31"
}
```

### Create Vehicle

```json
{
  "vehicleType": "CAR",
  "subtype": "ECONOMY",
  "licenseNumber": "01A123BC",
  "stockNumber": "STK-1001",
  "model": "Corolla",
  "make": "Toyota",
  "manufacturingYear": 2023,
  "mileage": 12000,
  "passengerCapacity": 5,
  "hasSunroof": false,
  "status": "AVAILABLE",
  "locationId": 1,
  "parkingStallId": 1,
  "barcode": "BC-0001"
}
```

### Create Reservation

```json
{
  "memberId": 1,
  "vehicleId": 1,
  "pickupLocationName": "Airport Branch",
  "returnLocationName": "City Center Branch",
  "dueDate": "2026-04-10T10:00:00"
}
```

### Create Payment

```json
{
  "billId": 1,
  "amount": 150.00,
  "status": "PENDING",
  "paymentType": "CREDIT_CARD",
  "nameOnCard": "John Doe"
}
```

### Add Vehicle Log

```json
{
  "vehicleId": 1,
  "type": "FUELING",
  "description": "Refueled before next rental"
}
```

## Example cURL Commands

Start the app first, then try:

```bash
curl -i http://localhost:8080/api/v1/vehicles
```

```bash
curl -i -X POST http://localhost:8080/api/v1/locations \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Airport Branch",
    "address": {
      "streetAddress": "123 Main St",
      "city": "Tashkent",
      "state": "Tashkent",
      "zipcode": "100000",
      "country": "Uzbekistan"
    }
  }'
```

Expected result for now:

- HTTP `501 Not Implemented`

That is normal because the endpoints are only scaffolded at this stage.

## Main Domain Model Already Added

The JPA model already includes classes for:

- rental system and rental locations
- vehicles and vehicle subtypes
- members and receptionists
- vehicle reservations
- parking stalls
- vehicle logs
- billing and bill items
- payments and payment subtypes
- notifications
- equipment
- services
- rental insurance
- additional drivers
- enums for statuses and types

## Running Tests

Current test coverage is minimal.

You can run:

```bash
mvn test
```

At the moment, the project only contains a basic Spring context load test.

## Suggested Next Steps

If you want to continue building this project, the next logical steps are:

1. implement service layer logic
2. connect controllers to repositories through services
3. add response DTOs and mappers
4. add exception handling
5. add real validation and business rules
6. create database migration scripts
7. add unit and integration tests

## Notes

- This repository currently does not include a Maven Wrapper (`mvnw`).
- You need Maven installed globally to run the commands above.
- The current README describes the project exactly as it works now, not the final expected behavior.
