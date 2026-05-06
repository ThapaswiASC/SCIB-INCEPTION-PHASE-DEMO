# MyProject - Task Management System

## Overview

This is a Spring Boot application implementing a comprehensive task management system with Kanban board functionality. The application supports task creation, status updates, validation, and column management.

## Features

### DEMO-222: Kanban Board Drag and Drop
- Update task status when dragging between columns
- Get task details
- Get column statistics
- Bulk update column counts
- Real-time status synchronization

### DEMO-757: High-Performance Task Management
- Create tasks with validation
- Support for 10,000+ tasks per user
- Paginated task retrieval
- Task count tracking
- Update and delete tasks
- Bulk task creation
- Optimistic locking for concurrent operations

### DEMO-759: Input Validation
- Comprehensive input validation
- Special character support
- Character limit enforcement (title: 255, description: 10,000)
- Validation endpoint for pre-submission checks
- Meaningful error messages

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
├── src/main/java/com/myproject/
│   ├── controllers/          # REST API endpoints
│   ├── models/
│   │   ├── dtos/            # Data Transfer Objects
│   │   ├── entities/        # JPA entities
│   │   └── datastores/      # Repository interfaces
│   ├── services/
│   │   ├── interfaces/      # Service interfaces
│   │   └── impl/            # Service implementations
│   ├── config/              # Configuration classes
│   ├── exceptions/          # Custom exceptions
│   └── Application.java     # Main application class
└── src/main/resources/
    └── application.properties
```

## API Endpoints

### Task Management (DEMO-222 & DEMO-757)

#### Update Task Status
```
PUT /api/v1/tasks/{taskId}/status
Content-Type: application/json

{
  "status": "DONE",
  "columnId": "done-column"
}
```

#### Get Task Details
```
GET /api/v1/tasks/{taskId}
```

#### Create Task
```
POST /api/v1/tasks
Content-Type: application/json

{
  "title": "Task Title",
  "description": "Task Description",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

#### Get User Tasks (Paginated)
```
GET /api/v1/users/{userId}/tasks?page=0&size=50
```

#### Get Task Count
```
GET /api/v1/users/{userId}/tasks/count
```

#### Update Task
```
PUT /api/v1/tasks/{taskId}/update
Content-Type: application/json

{
  "title": "Updated Title",
  "description": "Updated Description",
  "status": "IN_PROGRESS",
  "priority": "URGENT"
}
```

#### Delete Task
```
DELETE /api/v1/tasks/{taskId}/delete
```

#### Bulk Create Tasks
```
POST /api/v1/tasks/bulk
Content-Type: application/json

[
  {
    "title": "Task 1",
    "userId": 1,
    "priority": "LOW"
  },
  {
    "title": "Task 2",
    "userId": 1,
    "priority": "MEDIUM"
  }
]
```

### Column Management (DEMO-222)

#### Get Column Statistics
```
GET /api/v1/columns/{columnId}/stats
```

#### Bulk Update Column Counts
```
PUT /api/v1/columns/bulk-update
Content-Type: application/json

{
  "updates": [
    {
      "columnId": "in-progress",
      "increment": -1
    },
    {
      "columnId": "done",
      "increment": 1
    }
  ]
}
```

### Input Validation (DEMO-759)

#### Create Task with Validation
```
POST /api/tasks
Content-Type: application/json

{
  "title": "Validated Task",
  "description": "Task with comprehensive validation",
  "priority": "CRITICAL"
}
```

#### Validate Task Input
```
POST /api/tasks/validate
Content-Type: application/json

{
  "title": "Task to Validate",
  "priority": "HIGH"
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

### H2 Console
Access the H2 database console at: `http://localhost:8080/api/h2-console`
- JDBC URL: `jdbc:h2:mem:taskdb`
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

Coverage report will be available at: `target/site/jacoco/index.html`

## Configuration

Key configuration properties in `application.properties`:

```properties
# Application
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:h2:mem:taskdb

# Validation
app.validation.title.max-length=255
app.validation.description.max-length=10000
app.task.max-per-user=10000
```

## Error Handling

The application provides structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    "Title is required",
    "Priority must be one of: LOW, MEDIUM, HIGH, CRITICAL"
  ]
}
```

### Error Codes
- `VALIDATION_ERROR`: Input validation failed
- `TASK_NOT_FOUND`: Task does not exist
- `TASK_LIMIT_EXCEEDED`: User has reached maximum task limit
- `INVALID_STATUS_TRANSITION`: Invalid task status change
- `COLUMN_NOT_FOUND`: Column does not exist
- `INTERNAL_ERROR`: Unexpected server error

## Security

- CORS enabled for `http://localhost:4200`
- All endpoints under `/api/v1/**` and `/api/tasks/**` are publicly accessible (for demo purposes)
- H2 console is enabled for development

## Performance Considerations

- Database indexes on frequently queried fields (user_id, created_at, status)
- Optimistic locking for concurrent task updates
- Pagination support for large result sets
- Efficient bulk operations

## Future Enhancements

- Replace H2 with PostgreSQL for production
- Add JWT-based authentication
- Implement WebSocket for real-time updates
- Add Redis caching layer
- Implement rate limiting
- Add comprehensive audit logging

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please contact the development team.