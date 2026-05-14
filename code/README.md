# MyProject - Task Management System

## Overview

This is a Spring Boot application for managing tasks with support for Kanban board functionality. The application provides REST APIs for creating, updating, deleting, and retrieving tasks, as well as managing column statistics.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **User Task Management**: Retrieve tasks by user with pagination
- **Bulk Operations**: Create multiple tasks in a single request
- **Task Status Management**: Update task status and column assignments
- **Column Statistics**: Track task counts per column
- **Input Validation**: Comprehensive validation for all inputs
- **Error Handling**: Graceful error handling with detailed error messages

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
│       │   │   └── datastores/      # In-memory data stores
│       │   ├── services/
│       │   │   ├── interfaces/      # Service interfaces
│       │   │   └── impl/            # Service implementations
│       │   ├── config/              # Configuration classes
│       │   ├── exceptions/          # Custom exceptions
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml      # CI/CD workflow
```

## API Endpoints

### Task Management

- `POST /api/v1/tasks` - Create a new task
- `GET /api/v1/tasks` - List all tasks
- `GET /api/v1/tasks/{taskId}` - Get task by ID
- `PUT /api/v1/tasks/{taskId}` - Update task
- `DELETE /api/v1/tasks/{taskId}` - Delete task
- `PUT /api/v1/tasks/{taskId}/status` - Update task status
- `POST /api/v1/tasks/bulk` - Bulk create tasks

### User Task Management

- `GET /api/v1/users/{userId}/tasks` - Get user tasks (paginated)
- `GET /api/v1/users/{userId}/tasks/count` - Get user task count

### Column Management

- `GET /api/v1/columns/{columnId}/stats` - Get column statistics
- `PUT /api/v1/columns/bulk-update` - Bulk update column counts

## Building the Application

```bash
cd code
mvn clean install
```

## Running the Application

```bash
cd code
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## Running Tests

```bash
cd code
mvn test
```

## Generating Code Coverage Report

```bash
cd code
mvn jacoco:report
```

The coverage report will be available at `code/target/site/jacoco/index.html`

## Configuration

The application can be configured via `application.properties`:

```properties
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api
logging.level.root=INFO
logging.level.com.myproject=DEBUG
```

## CORS Configuration

The application is configured to allow CORS requests from:
- Origin: `http://localhost:4200`
- All HTTP methods
- All headers
- Credentials enabled

## Data Storage

The application uses in-memory data stores for:
- **Tasks**: Stored in `TaskDataStoreImpl`
- **Columns**: Stored in `ColumnDataStoreImpl` with default columns (To Do, In Progress, Done)

## Validation Rules

### Task Creation
- Title: Required, 1-255 characters, cannot be empty or whitespace-only
- Description: Optional, max 2000 characters
- User ID: Required, must be positive
- Priority: Optional, must be LOW, MEDIUM, HIGH, or URGENT

### Task Update
- Title: Optional, 1-255 characters if provided
- Description: Optional, max 10000 characters
- Priority: Optional, must be LOW, MEDIUM, or HIGH
- Status: Optional, must be TODO, IN_PROGRESS, or DONE

### Task Limits
- Maximum tasks per user: 10,000

## Error Handling

The application provides detailed error responses with:
- HTTP status code
- Error code
- Error message
- Timestamp
- Validation errors (if applicable)

### Common Error Codes
- `TASK_NOT_FOUND` - Task does not exist
- `COLUMN_NOT_FOUND` - Column does not exist
- `TASK_LIMIT_EXCEEDED` - User has reached maximum task limit
- `INVALID_STATUS_TRANSITION` - Invalid task status change
- `INVALID_INPUT` - Input validation failed
- `VALIDATION_ERROR` - Bean validation failed

## CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/build.yml`) that:
- Builds the application with Maven
- Runs tests
- Generates JaCoCo coverage reports
- Uploads test results and coverage reports as artifacts

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please refer to the project documentation or contact the development team.