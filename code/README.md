# SCIB Inception Phase Demo - MyProject

## Overview

This is a Spring Boot application implementing a task management system with Kanban board functionality. The application provides REST APIs for managing tasks, columns, and validating input data.

## Features

### Task Management (DEMO-757)
- Create, read, update, and delete tasks
- Support for up to 10,000 tasks per user
- Bulk task creation
- Paginated task retrieval
- Task count tracking per user

### Kanban Board (DEMO-222)
- Drag and drop task status updates
- Column statistics tracking
- Bulk column count updates
- Real-time task movement between columns

### Input Validation (DEMO-759)
- Comprehensive input validation
- Graceful handling of malformed data
- Detailed validation error messages
- Support for special characters and unicode

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Data Storage**: In-memory (ConcurrentHashMap)

## Project Structure

```
code/
├── src/main/java/com/myproject/
│   ├── controllers/          # REST API controllers
│   ├── services/
│   │   ├── interfaces/       # Service interfaces
│   │   └── impl/             # Service implementations
│   ├── models/
│   │   ├── dtos/             # Data Transfer Objects
│   │   ├── entities/         # Domain entities
│   │   └── datastores/       # Data access layer
│   ├── config/               # Configuration classes
│   ├── exceptions/           # Custom exceptions
│   └── Application.java      # Main application class
└── src/main/resources/
    └── application.properties # Application configuration
```

## API Endpoints

### Task Management

- `POST /api/v1/tasks` - Create a new task
- `GET /api/v1/tasks/{taskId}` - Get task details
- `PUT /api/v1/tasks/{taskId}/update` - Update task
- `DELETE /api/v1/tasks/{taskId}/delete` - Delete task
- `POST /api/v1/tasks/bulk` - Bulk create tasks
- `GET /api/v1/users/{userId}/tasks` - Get user tasks (paginated)
- `GET /api/v1/users/{userId}/tasks/count` - Get task count

### Kanban Board

- `PUT /api/v1/tasks/{taskId}/status` - Update task status
- `GET /api/v1/columns/{columnId}/stats` - Get column statistics
- `PUT /api/v1/columns/bulk-update` - Bulk update column counts

### Validation

- `POST /api/v1/tasks/validate` - Validate task input

## Building and Running

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

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

The application is configured to allow CORS requests from:
- `http://localhost:4200`

All headers and methods are allowed with credentials support.

## Error Handling

The application provides comprehensive error handling with structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "traceId": "uuid",
  "errorCode": "ERROR_CODE",
  "message": "Error message",
  "details": ["Additional details"]
}
```

## Validation Rules

### Task Creation
- Title: Required, 1-255 characters
- Description: Optional, max 2000 characters
- User ID: Required, positive number
- Priority: Required, one of LOW, MEDIUM, HIGH, URGENT
- Due Date: Optional, future date

### Task Validation Endpoint
- Title: Required, 1-255 characters
- Description: Optional, max 10000 characters
- Priority: Optional, must match pattern ^(HIGH|MEDIUM|LOW)$

## Business Rules

1. **Task Limit**: Each user can have a maximum of 10,000 tasks
2. **Status Transitions**: Tasks can move between TO_DO, IN_PROGRESS, and DONE
3. **Column Counts**: Automatically updated when tasks move between columns
4. **Concurrent Operations**: Thread-safe in-memory data stores using ConcurrentHashMap

## Testing

Run tests with:

```bash
mvn test
```

Generate coverage report:

```bash
mvn jacoco:report
```

Coverage reports are available at `target/site/jacoco/index.html`

## CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/build.yml`) that:
- Builds the application
- Runs tests
- Generates coverage reports
- Uploads artifacts

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please refer to the project documentation or contact the development team.