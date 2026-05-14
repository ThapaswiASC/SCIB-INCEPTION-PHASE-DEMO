# MyProject - SCIB Inception Phase Demo

## Overview

This is a Spring Boot 3.5.9 application built with Java 21 that implements a task management system with Kanban board functionality.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Kanban Board**: Drag-and-drop task status updates with column management
- **Input Validation**: Robust validation for malformed or unexpected input
- **High Performance**: Supports up to 10,000 tasks per user with optimized operations
- **In-Memory Storage**: Uses concurrent data structures for thread-safe operations

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Testing**: JUnit Jupiter, JaCoCo for coverage

## Project Structure

```
code/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/myproject/
│       │   ├── controllers/          # REST API endpoints
│       │   ├── models/
│       │   │   ├── dtos/            # Data Transfer Objects
│       │   │   ├── entities/        # Domain entities
│       │   │   └── datastores/      # In-memory data storage
│       │   ├── services/
│       │   │   ├── interfaces/      # Service contracts
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
- `GET /api/v1/tasks` - Get paginated list of tasks
- `GET /api/v1/tasks/{taskId}` - Get task by ID
- `PUT /api/v1/tasks/{taskId}` - Update task
- `DELETE /api/v1/tasks/{taskId}` - Delete task
- `POST /api/v1/tasks/bulk` - Bulk create tasks
- `PUT /api/v1/tasks/{taskId}/status` - Update task status

### User Tasks

- `GET /api/v1/users/{userId}/tasks` - Get user's tasks
- `GET /api/v1/users/{userId}/tasks/count` - Get user's task count

### Column Management

- `GET /api/v1/columns/{columnId}/stats` - Get column statistics
- `PUT /api/v1/columns/bulk-update` - Bulk update column counts

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
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api
logging.level.root=INFO
logging.level.com.myproject=DEBUG
```

## CORS Configuration

CORS is configured to allow requests from:
- `http://localhost:4200` (Angular development server)

## Validation Rules

### Task Creation

- **Title**: Required, 1-255 characters, cannot be whitespace-only
- **Description**: Optional, max 10,000 characters
- **User ID**: Required, must be positive
- **Priority**: Optional, valid values: LOW, MEDIUM, HIGH, URGENT

### Task Limits

- Maximum 10,000 tasks per user
- Validation enforced at service layer

### Status Transitions

Valid status transitions:
- TO_DO → IN_PROGRESS
- IN_PROGRESS → DONE
- IN_PROGRESS → TO_DO
- PENDING → IN_PROGRESS
- IN_PROGRESS → COMPLETED

## Error Handling

The application provides structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": ["Title is required"]
}
```

## CI/CD

GitHub Actions workflow is configured for:
- Building with Maven
- Running tests
- Generating JaCoCo coverage reports
- Uploading test results and coverage artifacts

## Development Notes

### In-Memory Storage

The application uses in-memory concurrent data structures:
- `ConcurrentHashMap` for thread-safe operations
- `AtomicLong` for ID generation
- Pre-initialized with default Kanban columns (To Do, In Progress, Done)

### Future Enhancements

- Database integration (PostgreSQL/MySQL)
- Authentication and authorization
- WebSocket support for real-time updates
- Redis caching
- Metrics and monitoring

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please refer to the project documentation.
