# Flight Data Management

A Spring Boot application for managing flight data with a RESTful API.

## Description

This application provides a comprehensive solution for managing flight data, including creating, updating, retrieving, searching, and deleting flight information. It integrates with an external flight data provider (CrazySupplier) to enhance search results.

## Features

* Create, update, delete, and retrieve flight information.
* Search for flights based on origin, destination, airline, and time filters.
* Integration with CrazySupplier for real-time flight data.
* RESTful API design with Swagger documentation.
* Docker support for easy deployment.

## Technologies Used

* Java 17+
* Spring Boot
* Spring Data JPA
* Spring Cache
* Spring Retry
* Swagger/OpenAPI
* Docker & Docker Compose

## Setup

### Prerequisites

* Java 17 or higher
* Maven
* Docker and Docker Compose (optional for containerized deployment)

### Running Locally

1. Clone the repository:

   ```bash
   git clone <repository_url>
   cd flight-data-management
   ```

2. Build the application:

   ```bash
   ./mvnw clean package
   ```

3. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

### Running with Docker

```bash
docker-compose up -d
```

This will start the application on port 8080.

## API Endpoints

* `POST /flights` - Create a new flight
* `PUT /flights/{id}` - Update an existing flight
* `GET /flights/{id}` - Get a flight by ID
* `GET /flights` - Get all flights
* `POST /flights/search` - Search for flights based on criteria
* `DELETE /flights/{id}` - Delete a flight

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

## Testing

Run the tests with:

```bash
./mvnw test
```

## Assumptions

* CrazySupplier data is only accessed via API and is not stored locally.
* Dates should be provided in the correct timezones as specified.
* The database must be preconfigured before running the application.
