# MyProject - SCIB Inception Phase Demo

## Overview

This is a Spring Boot application implementing a task management system with Kanban board functionality. The application provides RESTful APIs for managing tasks, columns, and user operations with comprehensive input validation and error handling.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Kanban Board**: Drag-and-drop task status updates with column management
- **Input Validation**: Robust validation for malformed or unexpected input data
- **Bulk Operations**: Support for bulk task creation and column updates
- **Pagination**: Efficient pagination for large task lists
- **Performance Optimized**: Supports up to 10,000 tasks per user with 200ms response time target

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Testing**: JUnit Jupiter, Spring Boot Test
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

- `GET /api/v1/users/{userId}/tasks` - Get paginated user tasks
- `GET /api/v1/users/{userId}/tasks/count` - Get user task count

### Kanban Board

- `PUT /api/v1/tasks/{taskId}/status` - Update task status
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

### H2 Console

Access the H2 database console at: `http://localhost:8080/api/h2-console`

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Testing

```bash
mvn test
```

### Code Coverage

```bash
mvn jacoco:report
```

Coverage reports are generated in `target/site/jacoco/`

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Task Limits
app.task.max-per-user=10000
app.task.creation-timeout=200ms

# Validation
validation.title.max-length=255
validation.description.max-length=10000
```

## Input Validation

The application handles various types of malformed input:

- **Null values**: Proper null checks and error messages
- **Whitespace-only strings**: Trimmed and validated
- **Special characters**: UTF-8 encoding support
- **Length constraints**: Title (1-255 chars), Description (max 10000 chars)
- **Task limits**: Maximum 10,000 tasks per user

## Error Handling

Standardized error responses with:

- Timestamp
- Trace ID for debugging
- Error code
- Human-readable message
- Detailed validation errors (when applicable)

## Performance Considerations

- Database indexing on frequently queried fields
- Optimistic locking for concurrent operations
- Pagination for large result sets
- Efficient bulk operations
- Connection pooling with HikariCP

## Security

- CORS configuration for frontend integration
- Input sanitization to prevent injection attacks
- JWT-ready security configuration (currently permissive for development)

## CI/CD

GitHub Actions workflow for:

- Building with Maven
- Running tests
- Generating code coverage reports
- Uploading artifacts

## License

This project is part of the SCIB Inception Phase Demo.

## Support

For issues and questions, please refer to the project documentation or contact the development team.
