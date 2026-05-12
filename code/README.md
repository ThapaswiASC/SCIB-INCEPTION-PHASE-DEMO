# Task Management System (myproject)

## Overview

A high-performance Spring Boot application for managing tasks with support for up to 10,000 tasks per user. The system is designed to handle large volumes of task creation while maintaining response times under 200ms.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Bulk Operations**: Create multiple tasks in a single request (up to 100 tasks)
- **User Task Tracking**: Track task counts per user with a 10,000 task limit
- **Performance Monitoring**: Built-in performance tracking and logging
- **Validation**: Comprehensive input validation with detailed error messages
- **In-Memory Storage**: Fast, executable without external database dependencies

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Testing**: JUnit Jupiter
- **Code Coverage**: JaCoCo

## API Endpoints

### Task Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/tasks` | Create a new task |
| GET | `/api/v1/users/{userId}/tasks` | Get all tasks for a user (paginated) |
| GET | `/api/v1/users/{userId}/tasks/count` | Get task count for a user |
| GET | `/api/v1/tasks/{taskId}` | Get a specific task by ID |
| PUT | `/api/v1/tasks/{taskId}` | Update an existing task |
| DELETE | `/api/v1/tasks/{taskId}` | Delete a task |
| POST | `/api/v1/tasks/bulk` | Bulk create tasks (max 100) |

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

### Generating Coverage Report

```bash
mvn jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## Configuration

Key configuration properties in `application.properties`:

```properties
# Application Settings
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api

# Performance Configuration
task.creation.performance.threshold=200
task.user.limit=10000
task.bulk.batch.size=100

# Logging
logging.level.root=INFO
logging.level.com.myproject=DEBUG
```

## Data Models

### Task Priority
- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

### Task Status
- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

## Error Handling

The application provides structured error responses:

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `TASK_LIMIT_EXCEEDED` | 400 | User has reached 10,000 task limit |
| `CONCURRENT_CREATION_ERROR` | 409 | Concurrent task creation detected |
| `PERFORMANCE_THRESHOLD_EXCEEDED` | 503 | Performance threshold exceeded |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `TASK_NOT_FOUND` | 404 | Task does not exist |

## Performance Considerations

- In-memory data store for fast access
- Concurrent data structures for thread safety
- Performance monitoring and logging
- Pagination support for large result sets
- Bulk operation support for efficiency

## CORS Configuration

CORS is configured to allow requests from:
- `http://localhost:4200` (Angular development server)

## Project Structure

```
code/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/myproject/
│       │   ├── controllers/          # REST controllers
│       │   ├── models/
│       │   │   ├── dtos/            # Data Transfer Objects
│       │   │   ├── entities/        # Domain entities
│       │   │   └── datastores/      # Data access layer
│       │   ├── services/
│       │   │   ├── interfaces/      # Service interfaces
│       │   │   └── impl/            # Service implementations
│       │   ├── config/              # Configuration classes
│       │   ├── exceptions/          # Custom exceptions
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/
    └── build.yml                     # CI/CD workflow
```

## CI/CD

The project includes a GitHub Actions workflow for:
- Building the application
- Running tests
- Generating coverage reports
- Uploading artifacts

Workflow can be triggered manually via `workflow_dispatch`.

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please refer to the project documentation or contact the development team.