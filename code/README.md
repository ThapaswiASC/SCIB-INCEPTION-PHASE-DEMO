# MyProject - Task Management System

## Overview

This is a Spring Boot application for managing tasks in a Kanban board system. The application supports task creation, status updates, drag-and-drop functionality, and bulk operations.

## Features

- **Task Management**: Create, read, update, and delete tasks
- **Status Transitions**: Move tasks between TODO, IN_PROGRESS, and DONE statuses
- **Bulk Operations**: Create multiple tasks in a single request
- **User Task Management**: Retrieve tasks by user with pagination
- **Column Management**: Track task counts per column
- **Validation**: Comprehensive input validation and business rule enforcement
- **Error Handling**: Structured error responses with detailed messages

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Data Storage**: In-memory (ConcurrentHashMap)

## API Endpoints

### Task Operations

- `PUT /api/v1/tasks/{taskId}/status` - Update task status
- `GET /api/v1/tasks/{taskId}` - Get task details
- `GET /api/v1/tasks?status={status}` - Get tasks by status
- `POST /api/v1/tasks/{taskId}/validate-move` - Validate task move
- `POST /api/v1/tasks` - Create a new task
- `PUT /api/v1/tasks/{taskId}` - Update task
- `DELETE /api/v1/tasks/{taskId}` - Delete task
- `POST /api/v1/tasks/bulk` - Bulk create tasks

### User Task Operations

- `GET /api/v1/users/{userId}/tasks` - Get user tasks (paginated)
- `GET /api/v1/users/{userId}/tasks/count` - Get user task count

### Column Operations

- `GET /api/v1/columns/{columnId}/count` - Get column task count

## Building the Project

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## Running Tests

```bash
mvn test
```

## Configuration

Application configuration is located in `src/main/resources/application.properties`:

```properties
spring.application.name=myproject
server.port=8080
server.servlet.context-path=/api
logging.level.root=INFO
logging.level.com.myproject=DEBUG
```

## Business Rules

### Task Status Transitions

- TODO → IN_PROGRESS
- IN_PROGRESS → DONE
- DONE → IN_PROGRESS (rollback)

### Task Limits

- Maximum 10,000 tasks per user
- Maximum 100 tasks per bulk creation request

### Validation Rules

- Task title: Required, 1-255 characters
- Task description: Optional, max 2000 characters
- User ID: Required, positive integer
- Priority: Required (LOW, MEDIUM, HIGH, URGENT)
- Column ID: Max 50 characters

## Error Codes

- `TASK_NOT_FOUND` - Task does not exist
- `INVALID_STATUS_TRANSITION` - Invalid status change
- `TASK_LIMIT_EXCEEDED` - User has reached maximum task limit
- `COLUMN_NOT_FOUND` - Column does not exist
- `VALIDATION_ERROR` - Input validation failed
- `INTERNAL_ERROR` - Unexpected server error

## CORS Configuration

The application allows CORS requests from:
- `http://localhost:4200`

All HTTP methods and headers are allowed.

## Project Structure

```
code/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/myproject/
│       │   ├── controllers/
│       │   ├── models/
│       │   │   ├── dtos/
│       │   │   ├── entities/
│       │   │   └── datastores/
│       │   ├── services/
│       │   │   ├── interfaces/
│       │   │   └── impl/
│       │   ├── config/
│       │   ├── exceptions/
│       │   └── Application.java
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml
```

## Development Notes

- The application uses in-memory storage for simplicity
- All timestamps are in ISO-8601 format
- Task IDs are auto-generated sequential numbers
- Concurrent operations are supported via ConcurrentHashMap

## License

This project is part of the SCIB Inception Phase Demo.

## Contact

For questions or issues, please refer to the project documentation.