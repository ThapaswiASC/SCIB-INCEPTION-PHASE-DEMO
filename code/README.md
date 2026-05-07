# MyProject - SCIB Inception Phase Demo

## Overview

This is a Spring Boot application that implements a task management system with Kanban board functionality. The application provides RESTful APIs for creating, updating, and managing tasks with support for drag-and-drop operations across Kanban columns.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Bulk Operations**: Create multiple tasks in a single request
- **User Task Management**: Retrieve tasks by user with pagination
- **Kanban Board**: Drag-and-drop task status updates with column statistics
- **Input Validation**: Comprehensive validation for all input data
- **Error Handling**: Structured error responses with detailed messages
- **Performance Optimized**: Supports up to 10,000 tasks per user

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Database**: H2 (in-memory)
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
│       │   │   ├── entities/        # JPA entities
│       │   │   └── datastores/      # Repository interfaces
│       │   ├── services/
│       │   │   ├── interfaces/      # Service interfaces
│       │   │   └── impl/            # Service implementations
│       │   ├── config/              # Configuration classes
│       │   ├── exceptions/          # Custom exceptions
│       │   ├── utils/               # Utility classes
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml      # CI/CD workflow
```

## API Endpoints

### Task Management

- `POST /api/v1/tasks` - Create a new task
- `GET /api/v1/tasks` - List all tasks
- `GET /api/v1/tasks/{id}` - Get task by ID
- `PUT /api/v1/tasks/{id}` - Update task
- `DELETE /api/v1/tasks/{id}` - Delete task
- `POST /api/v1/tasks/bulk` - Bulk create tasks

### User Tasks

- `GET /api/v1/users/{userId}/tasks` - Get user tasks (paginated)
- `GET /api/v1/users/{userId}/tasks/count` - Get user task count

### Kanban Board

- `PUT /api/v1/tasks/{taskId}/status` - Update task status
- `GET /api/v1/columns/{columnId}/stats` - Get column statistics
- `PUT /api/v1/columns/bulk-update` - Bulk update column counts

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Building the Application

```bash
cd code
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### Running Tests

```bash
mvn test
```

### Generating Code Coverage Report

```bash
mvn jacoco:report
```

The coverage report will be available at `target/site/jacoco/index.html`

## Configuration

The application can be configured through `application.properties`:

```properties
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# Logging
logging.level.root=INFO
logging.level.com.myproject=DEBUG
```

## Validation Rules

### Task Creation

- **Title**: Required, 1-255 characters, cannot be only whitespace
- **Description**: Optional, max 10,000 characters
- **User ID**: Required, must be positive
- **Priority**: Optional, valid values: LOW, MEDIUM, HIGH, URGENT
- **Task Limit**: Maximum 10,000 tasks per user

### Status Transitions

- `PENDING/TO_DO` → `IN_PROGRESS`
- `IN_PROGRESS` → `DONE/COMPLETED` or back to `TO_DO`
- `DONE/COMPLETED` → `IN_PROGRESS`

## Error Handling

The application provides structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "traceId": "uuid",
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": ["Title is required"]
}
```

### Error Codes

- `VALIDATION_ERROR` - Input validation failed
- `TASK_NOT_FOUND` - Task does not exist
- `TASK_LIMIT_EXCEEDED` - User has reached task limit
- `INVALID_STATUS_TRANSITION` - Invalid status change
- `INVALID_INPUT` - Invalid input data

## Security

- CORS enabled for `http://localhost:4200`
- All endpoints under `/api/v1/**` are publicly accessible
- H2 console available at `/api/h2-console`

## Database

The application uses H2 in-memory database. The database is automatically created on startup and destroyed on shutdown.

### Accessing H2 Console

1. Navigate to `http://localhost:8080/api/h2-console`
2. Use the following connection details:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (leave empty)

## CI/CD

The project includes a GitHub Actions workflow for automated builds:

- Triggered manually via `workflow_dispatch`
- Builds with Maven
- Generates JaCoCo coverage reports
- Uploads test results and coverage reports as artifacts

## License

This project is part of the SCIB Inception Phase Demo.

## Support

For issues and questions, please refer to the project documentation or contact the development team.