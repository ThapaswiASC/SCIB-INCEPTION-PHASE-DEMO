# MyProject - SCIB Inception Phase Demo

A comprehensive Spring Boot application implementing task management and Kanban board functionality with robust input validation and error handling.

## Overview

This project implements three main features:

1. **Kanban Board Operations (DEMO-222)**: Drag-and-drop task management with status transitions and column statistics
2. **High-Performance Task Management (DEMO-757)**: Support for up to 10,000 tasks per user with optimized performance
3. **Input Validation (DEMO-759)**: Comprehensive input validation with special character support and graceful error handling

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Database**: H2 (in-memory)
- **Testing**: JUnit Jupiter
- **Code Coverage**: JaCoCo

## Project Structure

```
code/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/myproject/
│       │   ├── controllers/          # REST API endpoints
│       │   │   ├── TaskController.java
│       │   │   ├── ColumnController.java
│       │   │   └── ValidationController.java
│       │   ├── models/
│       │   │   ├── dtos/            # Data Transfer Objects
│       │   │   ├── entities/        # Domain entities
│       │   │   └── datastores/      # In-memory data storage
│       │   ├── services/
│       │   │   ├── interfaces/      # Service contracts
│       │   │   └── impl/            # Service implementations
│       │   ├── config/              # Configuration classes
│       │   ├── exceptions/          # Custom exceptions
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml      # CI/CD pipeline
```

## API Endpoints

### Kanban Board Operations

- `PUT /api/v1/tasks/{taskId}/status` - Update task status
- `GET /api/v1/tasks/{taskId}` - Get task details
- `GET /api/v1/columns/{columnId}/stats` - Get column statistics
- `PUT /api/v1/columns/bulk-update` - Bulk update column counts

### Task Management

- `POST /api/v1/tasks` - Create a new task
- `GET /api/v1/users/{userId}/tasks` - Get user tasks (paginated)
- `GET /api/v1/users/{userId}/tasks/count` - Get task count for user
- `PUT /api/v1/tasks/{taskId}/update` - Update task
- `DELETE /api/v1/tasks/{taskId}/delete` - Delete task
- `POST /api/v1/tasks/bulk` - Bulk create tasks

### Input Validation

- `POST /api/tasks` - Create task with validation
- `POST /api/tasks/validate` - Validate task input without creating

## Features

### 1. Kanban Board Management

- Drag-and-drop task status updates
- Column statistics tracking
- Atomic bulk column count updates
- Status transition validation

### 2. Task Management

- Support for 10,000+ tasks per user
- Paginated task retrieval
- Bulk task creation
- Task count tracking
- Optimistic locking for concurrent operations

### 3. Input Validation

- Comprehensive field validation
- Special character support (UTF-8)
- XSS prevention through input sanitization
- Character limit enforcement (255 for title, 10,000 for description)
- Whitespace handling

## Configuration

### Application Properties

```properties
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### CORS Configuration

- Allowed Origin: `http://localhost:4200`
- Credentials: Enabled
- All headers and methods allowed

## Building and Running

### Prerequisites

- JDK 21
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

### Access H2 Console

Navigate to `http://localhost:8080/api/h2-console`

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Testing

### Run Tests

```bash
mvn test
```

### Generate Coverage Report

```bash
mvn jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## CI/CD Pipeline

The project includes a GitHub Actions workflow that:

1. Checks out the code
2. Sets up JDK 21
3. Builds the project with Maven
4. Generates JaCoCo coverage report
5. Uploads test results and coverage reports as artifacts

### Trigger Workflow

The workflow can be manually triggered via GitHub Actions UI (workflow_dispatch).

## Error Handling

The application provides comprehensive error handling with structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "traceId": "uuid",
  "errorCode": "ERROR_CODE",
  "message": "Error description",
  "details": ["Additional error details"]
}
```

### Error Codes

- `TASK_NOT_FOUND` - Task does not exist
- `COLUMN_NOT_FOUND` - Column does not exist
- `USER_NOT_FOUND` - User does not exist
- `INVALID_STATUS_TRANSITION` - Invalid task status change
- `TASK_LIMIT_EXCEEDED` - User has reached maximum task limit
- `INVALID_INPUT` - Input validation failed
- `VALIDATION_ERROR` - Bean validation failed

## Data Storage

The application uses in-memory data stores for:

- **Tasks**: Concurrent hash map with atomic ID generation
- **Columns**: Pre-initialized with default Kanban columns (To Do, In Progress, Done)
- **Users**: Pre-initialized with a test user

This design allows the application to run without external database dependencies while maintaining data consistency.

## Performance Considerations

- Connection pooling with HikariCP
- Paginated queries for large datasets
- Optimistic locking for concurrent updates
- Efficient in-memory data structures
- Response time target: < 200ms for 95% of requests

## Security

- CSRF protection disabled for API endpoints
- Stateless session management
- Input sanitization to prevent XSS attacks
- All endpoints currently permit all (authentication can be enabled)

## Validation Rules

### Task Title
- Required
- 1-255 characters
- Cannot be empty or whitespace only
- Special characters allowed

### Task Description
- Optional
- Maximum 10,000 characters
- Special characters allowed

### Task Priority
- Required
- Values: LOW, MEDIUM, HIGH, URGENT

### Task Status
- Required
- Values: PENDING, IN_PROGRESS, COMPLETED, CANCELLED (for validated tasks)
- Values: TO_DO, IN_PROGRESS, DONE (for Kanban tasks)

## Future Enhancements

- Database persistence (PostgreSQL/MySQL)
- JWT-based authentication
- WebSocket support for real-time updates
- Redis caching
- Internationalization (i18n)
- Advanced search and filtering
- Task assignment and collaboration features

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please contact the development team.