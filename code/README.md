# Task Management System

## Overview

This is a Spring Boot application for task management with comprehensive error handling. The system provides RESTful APIs for creating, reading, updating, and deleting tasks with robust error handling for database connection failures, timeouts, and validation errors.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Comprehensive Error Handling**: Handles database errors, timeouts, and validation errors
- **Input Preservation**: Preserves user input during error scenarios for retry
- **In-Memory Data Store**: Uses in-memory storage for demonstration (easily replaceable with database)
- **Retry Mechanism**: Automatic retry for transient failures
- **Validation**: Input validation with detailed error messages
- **CORS Support**: Configured for frontend integration

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Testing**: JUnit Jupiter
- **Code Coverage**: JaCoCo

## Project Structure

```
code/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/myproject/
│       │   ├── controllers/          # REST API controllers
│       │   ├── models/
│       │   │   ├── dtos/            # Data Transfer Objects
│       │   │   ├── entities/        # Domain entities
│       │   │   └── datastores/      # Data access layer
│       │   ├── services/
│       │   │   ├── interfaces/      # Service interfaces
│       │   │   └── impl/            # Service implementations
│       │   ├── config/              # Configuration classes
│       │   ├── exceptions/          # Custom exceptions and handlers
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml      # CI/CD workflow
```

## API Endpoints

### Create Task
- **Method**: POST
- **Path**: `/api/tasks`
- **Request Body**:
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59Z"
}
```
- **Response**: 201 Created

### Get Task
- **Method**: GET
- **Path**: `/api/tasks/{id}`
- **Response**: 200 OK

### Update Task
- **Method**: PUT
- **Path**: `/api/tasks/{id}`
- **Request Body**:
```json
{
  "title": "Updated title",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM"
}
```
- **Response**: 200 OK

### Delete Task
- **Method**: DELETE
- **Path**: `/api/tasks/{id}`
- **Response**: 204 No Content

## Error Handling

The application provides comprehensive error handling with the following error codes:

- **SERVICE_UNAVAILABLE** (503): Database connection failure
- **REQUEST_TIMEOUT** (408): Operation timeout
- **VALIDATION_ERROR** (400): Input validation failure
- **NOT_FOUND** (404): Resource not found
- **INTERNAL_SERVER_ERROR** (500): Unexpected server error

### Error Response Format
```json
{
  "code": "SERVICE_UNAVAILABLE",
  "message": "Service temporarily unavailable, please try again later",
  "timestamp": "2024-01-15T10:30:00Z",
  "preservedInput": { ... },
  "details": [
    "Database connection failed",
    "Retry after 30 seconds"
  ]
}
```

## Building and Running

### Prerequisites
- Java 21
- Maven 3.6+

### Build
```bash
cd code
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### Run Tests
```bash
mvn test
```

### Generate Coverage Report
```bash
mvn jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## Configuration

Key configuration properties in `application.properties`:

```properties
# Application
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
logging.level.com.myproject=DEBUG

# Error Handling
app.error.retry.max-attempts=3
app.error.retry.delay=1000
app.error.preserve-input=true
```

## CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/build.yml`) that:
- Builds the application
- Runs tests
- Generates code coverage reports
- Uploads artifacts

## Development Notes

### Data Storage
The application uses an in-memory data store by default. To integrate with a database:
1. Add database dependency to `pom.xml`
2. Implement database-backed version of `TaskDataStore`
3. Update configuration in `application.properties`

### Adding New Features
1. Define DTOs in `models/dtos`
2. Create service interface in `services/interfaces`
3. Implement service in `services/impl`
4. Add controller endpoint in `controllers`
5. Add appropriate exception handling

## License

This project is for demonstration purposes.

## Contact

For questions or issues, please contact the development team.