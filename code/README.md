# MyProject - Task Management System

## Overview

This is a Spring Boot application implementing a task management system with Kanban board functionality. The application supports:

- Task creation, update, and deletion
- Drag-and-drop task status updates
- Column statistics and bulk updates
- User task management with pagination
- High-performance operations supporting up to 10,000 tasks per user

## Technical Stack

- **Java**: 21
- **Spring Boot**: 3.5.9
- **Build Tool**: Maven
- **Architecture**: RESTful API with in-memory data storage

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
│       │   └── Application.java     # Main application class
│       └── resources/
│           └── application.properties
└── .github/workflows/build.yml      # CI/CD workflow
```

## API Endpoints

### Task Management (DEMO-222 & DEMO-757)

#### Update Task Status
- **PUT** `/api/v1/tasks/{taskId}/status`
- Updates task status when dragged between columns

#### Get Task Details
- **GET** `/api/v1/tasks/{taskId}`
- Retrieves detailed information about a specific task

#### Create Task
- **POST** `/api/v1/tasks`
- Creates a new task for a user

#### Get User Tasks
- **GET** `/api/v1/users/{userId}/tasks`
- Retrieves paginated list of tasks for a user
- Query params: `page`, `size`, `sort`

#### Get Task Count
- **GET** `/api/v1/users/{userId}/tasks/count`
- Returns total task count for a user

#### Update Task
- **PUT** `/api/v1/tasks/{taskId}`
- Updates an existing task

#### Delete Task
- **DELETE** `/api/v1/tasks/{taskId}`
- Deletes a task

#### Bulk Create Tasks
- **POST** `/api/v1/tasks/bulk`
- Creates multiple tasks in a single operation (max 100)

### Column Management (DEMO-222)

#### Get Column Statistics
- **GET** `/api/v1/columns/{columnId}/stats`
- Retrieves statistics for a column

#### Bulk Update Column Counts
- **PUT** `/api/v1/columns/bulk-update`
- Updates task counts for multiple columns atomically

## Configuration

The application runs on:
- **Port**: 8080
- **Context Path**: `/api`
- **CORS**: Enabled for `http://localhost:4200`

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

The application will start at `http://localhost:8080/api`

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

Trigger manually via GitHub Actions UI using `workflow_dispatch`.

## Features

### DEMO-222: Kanban Board Drag & Drop
- Drag tasks between columns (To Do, In Progress, Done)
- Automatic status updates
- Column count synchronization
- Validation of status transitions

### DEMO-757: High-Performance Task Management
- Support for 10,000+ tasks per user
- Efficient pagination
- Bulk operations
- Optimistic locking for concurrent updates
- Performance monitoring

## Error Handling

The application provides structured error responses:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "traceId": "uuid",
  "errorCode": "TASK_NOT_FOUND",
  "message": "Task not found with ID: 123",
  "details": []
}
```

### Error Codes
- `TASK_NOT_FOUND`: Task does not exist
- `INVALID_STATUS_TRANSITION`: Invalid status change
- `COLUMN_NOT_FOUND`: Column does not exist
- `VALIDATION_ERROR`: Request validation failed
- `TASK_LIMIT_EXCEEDED`: User task limit reached
- `CONCURRENT_CREATION_ERROR`: Concurrent modification conflict
- `PERFORMANCE_THRESHOLD_EXCEEDED`: Service overloaded

## Data Storage

The application uses in-memory data stores:
- **TaskDataStore**: Manages task entities
- **ColumnDataStore**: Manages column entities

Default columns are pre-initialized:
- `to-do`: To Do
- `in-progress`: In Progress
- `done`: Done

## Validation Rules

### Task Creation
- Title: Required, 1-255 characters
- Description: Optional, max 2000 characters
- User ID: Required, positive number
- Priority: Required (LOW, MEDIUM, HIGH, URGENT)
- Due Date: Optional, future or present

### Task Limits
- Maximum tasks per user: 10,000
- Bulk creation limit: 100 tasks
- Pagination max size: 100

## Status Transitions

Valid task status transitions:
- `TO_DO` → `IN_PROGRESS`
- `IN_PROGRESS` → `DONE`
- `IN_PROGRESS` → `TO_DO`
- `PENDING` → `IN_PROGRESS`
- `IN_PROGRESS` → `COMPLETED`
- `IN_PROGRESS` → `CANCELLED`

## License

This project is part of the SCIB Inception Phase Demo.

## Support

For issues or questions, please refer to the project documentation or contact the development team.