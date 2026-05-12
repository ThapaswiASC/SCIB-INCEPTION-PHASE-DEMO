# Task Management System

## Overview

High-performance task management system supporting creation of at least 10,000 tasks per user with optimal performance and concurrent processing capabilities.

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
│       │   ├── exceptions/          # Custom exceptions
│       │   ├── utils/               # Utility classes
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml      # CI/CD workflow
```

## Features

### API Endpoints

- **POST** `/api/v1/tasks` - Create a new task
- **POST** `/api/v1/tasks/bulk` - Bulk create tasks
- **GET** `/api/v1/tasks/{taskId}` - Get task by ID
- **PUT** `/api/v1/tasks/{taskId}` - Update task
- **DELETE** `/api/v1/tasks/{taskId}` - Delete task
- **GET** `/api/v1/users/{userId}/tasks` - Get user tasks (with pagination)
- **GET** `/api/v1/users/{userId}/tasks/count` - Get task count

### Key Features

- **High Performance**: Task creation within 200ms threshold
- **Scalability**: Support for 10,000+ tasks per user
- **Concurrent Processing**: Thread-safe operations with optimistic locking
- **In-Memory Storage**: Fast data access without database dependency
- **Performance Monitoring**: Built-in performance tracking
- **Validation**: Comprehensive input validation
- **Error Handling**: Structured error responses
- **CORS Support**: Configured for frontend integration

## Configuration

### Application Properties

```properties
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api

# Task Configuration
app.task.max-per-user=10000
app.performance.threshold-ms=200
app.concurrency.max-threads=50
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

### Build with Tests

```bash
mvn clean verify
```

### Generate Coverage Report

```bash
mvn jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## API Documentation

The API follows the OpenAPI 3.0 specification. See `api/openapi_scib_inception_phase_demo.yaml` for complete API documentation.

## Data Models

### Task Priority
- LOW
- MEDIUM
- HIGH

### Task Status
- PENDING
- IN_PROGRESS
- COMPLETED
- CANCELLED

## Performance Characteristics

- **Response Time**: < 200ms for task creation
- **Throughput**: 1000+ concurrent task creations per second
- **Capacity**: 10,000 tasks per user
- **Concurrency**: Thread-safe with optimistic locking

## Error Handling

The API returns structured error responses:

```json
{
  "timestamp": 1705320000000,
  "traceId": "abc123-def456-ghi789",
  "errorCode": "TASK_LIMIT_EXCEEDED",
  "message": "User has reached maximum task limit of 10000",
  "details": []
}
```

### Error Codes

- `TASK_LIMIT_EXCEEDED` - User has reached maximum task limit
- `CONCURRENT_CREATION_ERROR` - Concurrent modification detected
- `PERFORMANCE_THRESHOLD_EXCEEDED` - Operation exceeded performance threshold
- `TASK_NOT_FOUND` - Task not found
- `UNAUTHORIZED` - Authentication required
- `VALIDATION_ERROR` - Input validation failed

## CI/CD

The project includes a GitHub Actions workflow for automated build and testing:

- Builds the project with Maven
- Runs tests
- Generates JaCoCo coverage reports
- Uploads test results and coverage reports as artifacts

## Development Notes

### In-Memory Storage

The current implementation uses in-memory data stores for demonstration purposes. The datastore interfaces are designed to be easily replaced with database implementations (JPA, MongoDB, etc.) without changing the service layer.

### Authentication

The current implementation includes a mock authentication helper. In production, this should be replaced with proper JWT token validation and Spring Security integration.

### Future Enhancements

- Database integration (PostgreSQL/MongoDB)
- Redis caching for performance optimization
- JWT authentication and authorization
- Rate limiting
- Distributed tracing
- Metrics and monitoring integration

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please refer to the project documentation or contact the development team.