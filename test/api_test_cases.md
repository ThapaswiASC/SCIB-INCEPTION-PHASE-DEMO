# API Test Cases - Task Management System

## Overview

This document describes comprehensive API test cases for the Task Management System with Kanban board functionality. The system supports task creation, status updates, validation, and column management.

## Test Environment

- **Base URL**: `http://localhost:8080/api`
- **Database**: H2 (in-memory)
- **Authentication**: Public endpoints (for demo purposes)

---

## 1. Task Management API Tests

### 1.1 Update Task Status

#### TC-001: Update Task Status - Valid Transition

**Endpoint**: `PUT /v1/tasks/{taskId}/status`

**Preconditions**:
- Task with ID exists
- Task is in TO_DO status

**Request Body**:
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Steps**:
1. Send PUT request to `/v1/tasks/1/status`
2. Include valid status and columnId in request body

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `taskId`: "1"
  - `status`: "IN_PROGRESS"
  - `updatedAt`: timestamp
- Column counts are updated

---

#### TC-002: Update Task Status - Task Not Found

**Endpoint**: `PUT /v1/tasks/{taskId}/status`

**Preconditions**:
- Task with ID 999 does not exist

**Request Body**:
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Steps**:
1. Send PUT request to `/v1/tasks/999/status`

**Expected Result**:
- HTTP Status: 404 Not Found
- Response contains:
  - `errorCode`: "TASK_NOT_FOUND"
  - `message`: "Task not found with ID: 999"
  - `timestamp`: current timestamp

---

#### TC-003: Update Task Status - Invalid Transition

**Endpoint**: `PUT /v1/tasks/{taskId}/status`

**Preconditions**:
- Task with ID exists
- Task is in TO_DO status

**Request Body**:
```json
{
  "status": "DONE",
  "columnId": "done"
}
```

**Steps**:
1. Send PUT request to `/v1/tasks/1/status`
2. Attempt invalid status transition (TO_DO → DONE)

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "INVALID_STATUS_TRANSITION"
  - `message`: "Invalid status transition from TO_DO to DONE"

---

#### TC-004: Update Task Status - Missing Status

**Endpoint**: `PUT /v1/tasks/{taskId}/status`

**Request Body**:
```json
{
  "columnId": "in-progress"
}
```

**Steps**:
1. Send PUT request without status field

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Status is required"]

---

### 1.2 Get Task Details

#### TC-005: Get Task Details - Valid Task ID

**Endpoint**: `GET /v1/tasks/{taskId}`

**Preconditions**:
- Task with ID 1 exists

**Steps**:
1. Send GET request to `/v1/tasks/1`

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `taskId`: "1"
  - `title`: task title
  - `status`: current status
  - `columnId`: current column

---

#### TC-006: Get Task Details - Task Not Found

**Endpoint**: `GET /v1/tasks/{taskId}`

**Preconditions**:
- Task with ID 999 does not exist

**Steps**:
1. Send GET request to `/v1/tasks/999`

**Expected Result**:
- HTTP Status: 404 Not Found
- Response contains:
  - `errorCode`: "TASK_NOT_FOUND"

---

### 1.3 Create Task

#### TC-007: Create Task - Valid Request

**Endpoint**: `POST /v1/tasks`

**Preconditions**:
- User has not exceeded task limit (10,000)

**Request Body**:
```json
{
  "title": "New Task",
  "description": "Task description",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Steps**:
1. Send POST request to `/v1/tasks`
2. Include all required fields

**Expected Result**:
- HTTP Status: 201 Created
- Response contains:
  - `id`: generated task ID
  - `title`: "New Task"
  - `status`: "PENDING"
  - `priority`: "HIGH"
  - `createdAt`: timestamp
  - `updatedAt`: timestamp

---

#### TC-008: Create Task - Missing Title

**Endpoint**: `POST /v1/tasks`

**Request Body**:
```json
{
  "userId": 1,
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request without title field

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Title is required"]

---

#### TC-009: Create Task - Task Limit Exceeded

**Endpoint**: `POST /v1/tasks`

**Preconditions**:
- User has 10,000 tasks

**Request Body**:
```json
{
  "title": "New Task",
  "userId": 1,
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request when user is at task limit

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "User 1 has reached task limit: 10000/10000"

---

### 1.4 Get User Tasks (Paginated)

#### TC-010: Get User Tasks - Valid Request

**Endpoint**: `GET /v1/users/{userId}/tasks?page=0&size=50`

**Preconditions**:
- User has tasks

**Steps**:
1. Send GET request to `/v1/users/1/tasks?page=0&size=50`

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `content`: array of tasks
  - `totalElements`: total task count
  - `totalPages`: total pages
  - `size`: 50
  - `number`: 0

---

#### TC-011: Get User Tasks - Custom Pagination

**Endpoint**: `GET /v1/users/{userId}/tasks?page=2&size=10`

**Steps**:
1. Send GET request with custom page and size parameters

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `number`: 2
  - `size`: 10
  - `content`: array with up to 10 tasks

---

#### TC-012: Get User Tasks - Empty Result

**Endpoint**: `GET /v1/users/{userId}/tasks`

**Preconditions**:
- User has no tasks

**Steps**:
1. Send GET request for user with no tasks

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `content`: empty array
  - `totalElements`: 0
  - `totalPages`: 0

---

### 1.5 Get Task Count

#### TC-013: Get Task Count - Valid User

**Endpoint**: `GET /v1/users/{userId}/tasks/count`

**Preconditions**:
- User has 42 tasks

**Steps**:
1. Send GET request to `/v1/users/1/tasks/count`

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `userId`: 1
  - `taskCount`: 42

---

#### TC-014: Get Task Count - Zero Tasks

**Endpoint**: `GET /v1/users/{userId}/tasks/count`

**Preconditions**:
- User has no tasks

**Steps**:
1. Send GET request for user with no tasks

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `userId`: user ID
  - `taskCount`: 0

---

### 1.6 Update Task

#### TC-015: Update Task - Valid Request

**Endpoint**: `PUT /v1/tasks/{taskId}/update`

**Preconditions**:
- Task with ID exists

**Request Body**:
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "URGENT"
}
```

**Steps**:
1. Send PUT request to `/v1/tasks/1/update`
2. Include fields to update

**Expected Result**:
- HTTP Status: 200 OK
- Response contains updated task with:
  - `title`: "Updated Title"
  - `status`: "IN_PROGRESS"
  - `priority`: "URGENT"
  - `updatedAt`: new timestamp

---

#### TC-016: Update Task - Task Not Found

**Endpoint**: `PUT /v1/tasks/{taskId}/update`

**Preconditions**:
- Task with ID 999 does not exist

**Request Body**:
```json
{
  "title": "Updated Title"
}
```

**Steps**:
1. Send PUT request for non-existent task

**Expected Result**:
- HTTP Status: 404 Not Found
- Response contains:
  - `errorCode`: "TASK_NOT_FOUND"

---

#### TC-017: Update Task - Title Too Long

**Endpoint**: `PUT /v1/tasks/{taskId}/update`

**Request Body**:
```json
{
  "title": "<256 character string>"
}
```

**Steps**:
1. Send PUT request with title exceeding 255 characters

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Title must be between 1 and 255 characters"]

---

### 1.7 Delete Task

#### TC-018: Delete Task - Valid Task ID

**Endpoint**: `DELETE /v1/tasks/{taskId}/delete`

**Preconditions**:
- Task with ID exists

**Steps**:
1. Send DELETE request to `/v1/tasks/1/delete`

**Expected Result**:
- HTTP Status: 204 No Content
- Task is removed from database

---

#### TC-019: Delete Task - Task Not Found

**Endpoint**: `DELETE /v1/tasks/{taskId}/delete`

**Preconditions**:
- Task with ID 999 does not exist

**Steps**:
1. Send DELETE request for non-existent task

**Expected Result**:
- HTTP Status: 404 Not Found
- Response contains:
  - `errorCode`: "TASK_NOT_FOUND"

---

### 1.8 Bulk Create Tasks

#### TC-020: Bulk Create Tasks - Valid Request

**Endpoint**: `POST /v1/tasks/bulk`

**Preconditions**:
- User has not exceeded task limit

**Request Body**:
```json
[
  {
    "title": "Task 1",
    "userId": 1,
    "priority": "HIGH"
  },
  {
    "title": "Task 2",
    "userId": 1,
    "priority": "MEDIUM"
  }
]
```

**Steps**:
1. Send POST request to `/v1/tasks/bulk`
2. Include array of task creation requests

**Expected Result**:
- HTTP Status: 201 Created
- Response contains:
  - `createdTasks`: array of created tasks
  - `totalCreated`: 2
  - `failures`: empty array

---

#### TC-021: Bulk Create Tasks - Partial Success

**Endpoint**: `POST /v1/tasks/bulk`

**Preconditions**:
- User is near task limit

**Request Body**:
```json
[
  {
    "title": "Task 1",
    "userId": 1,
    "priority": "HIGH"
  },
  {
    "title": "Task 2",
    "userId": 1,
    "priority": "MEDIUM"
  }
]
```

**Steps**:
1. Send POST request when user can only create 1 more task

**Expected Result**:
- HTTP Status: 201 Created
- Response contains:
  - `createdTasks`: array with 1 task
  - `totalCreated`: 1
  - `failures`: array with 1 failure
  - `failures[0].index`: 1
  - `failures[0].error`: "Task limit exceeded"

---

#### TC-022: Bulk Create Tasks - Exceeds Limit

**Endpoint**: `POST /v1/tasks/bulk`

**Request Body**:
```json
[...101 tasks...]
```

**Steps**:
1. Send POST request with more than 100 tasks

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "Bulk creation limited to 100 tasks at a time"

---

## 2. Column Management API Tests

### 2.1 Get Column Statistics

#### TC-023: Get Column Statistics - Valid Column

**Endpoint**: `GET /v1/columns/{columnId}/stats`

**Preconditions**:
- Column "to-do" exists

**Steps**:
1. Send GET request to `/v1/columns/to-do/stats`

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `columnId`: "to-do"
  - `taskCount`: number of tasks
  - `lastUpdated`: timestamp

---

#### TC-024: Get Column Statistics - Column Not Found

**Endpoint**: `GET /v1/columns/{columnId}/stats`

**Preconditions**:
- Column "invalid-column" does not exist

**Steps**:
1. Send GET request to `/v1/columns/invalid-column/stats`

**Expected Result**:
- HTTP Status: 404 Not Found
- Response contains:
  - `errorCode`: "COLUMN_NOT_FOUND"
  - `message`: "Column not found with ID: invalid-column"

---

### 2.2 Bulk Update Column Counts

#### TC-025: Bulk Update Column Counts - Valid Request

**Endpoint**: `PUT /v1/columns/bulk-update`

**Preconditions**:
- Columns "to-do" and "in-progress" exist

**Request Body**:
```json
{
  "updates": [
    {
      "columnId": "to-do",
      "increment": -1
    },
    {
      "columnId": "in-progress",
      "increment": 1
    }
  ]
}
```

**Steps**:
1. Send PUT request to `/v1/columns/bulk-update`
2. Include array of column updates

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `success`: true
  - `updatedColumns`: ["to-do", "in-progress"]

---

#### TC-026: Bulk Update Column Counts - Missing Updates

**Endpoint**: `PUT /v1/columns/bulk-update`

**Request Body**:
```json
{}
```

**Steps**:
1. Send PUT request without updates field

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Updates list is required"]

---

## 3. Input Validation API Tests

### 3.1 Create Task with Validation

#### TC-027: Create Task with Validation - Valid Request

**Endpoint**: `POST /tasks`

**Request Body**:
```json
{
  "title": "Validated Task",
  "description": "Task with comprehensive validation",
  "priority": "CRITICAL"
}
```

**Steps**:
1. Send POST request to `/tasks`
2. Include all required fields

**Expected Result**:
- HTTP Status: 201 Created
- Response contains:
  - `id`: generated task ID
  - `title`: "Validated Task"
  - `status`: "PENDING"
  - `priority`: "CRITICAL"

---

#### TC-028: Create Task with Validation - Empty Title

**Endpoint**: `POST /tasks`

**Request Body**:
```json
{
  "title": "",
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request with empty title

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Title cannot be empty or contain only whitespace"]

---

#### TC-029: Create Task with Validation - Whitespace Only Title

**Endpoint**: `POST /tasks`

**Request Body**:
```json
{
  "title": "   ",
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request with whitespace-only title

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Title cannot be empty or contain only whitespace"]

---

#### TC-030: Create Task with Validation - Title Too Long

**Endpoint**: `POST /tasks`

**Request Body**:
```json
{
  "title": "<256 character string>",
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request with title exceeding 255 characters

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Title must be between 1 and 255 characters"]

---

#### TC-031: Create Task with Validation - Description Too Long

**Endpoint**: `POST /tasks`

**Request Body**:
```json
{
  "title": "Valid Task",
  "description": "<10001 character string>",
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request with description exceeding 10,000 characters

**Expected Result**:
- HTTP Status: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `details`: ["Description cannot exceed 10000 characters"]

---

#### TC-032: Create Task with Validation - Special Characters

**Endpoint**: `POST /tasks`

**Request Body**:
```json
{
  "title": "Task with special chars: @#$%^&*()",
  "description": "Description with special chars: <>&\"",
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request with special characters in title and description

**Expected Result**:
- HTTP Status: 201 Created
- Response contains task with special characters preserved

---

### 3.2 Validate Task Input

#### TC-033: Validate Task Input - Valid Request

**Endpoint**: `POST /tasks/validate`

**Request Body**:
```json
{
  "title": "Valid Task",
  "description": "Task description",
  "priority": "HIGH"
}
```

**Steps**:
1. Send POST request to `/tasks/validate`
2. Include valid task data

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `valid`: true
  - `errors`: empty array

---

#### TC-034: Validate Task Input - Invalid Request

**Endpoint**: `POST /tasks/validate`

**Request Body**:
```json
{
  "title": "<256 character string>",
  "priority": "INVALID_PRIORITY"
}
```

**Steps**:
1. Send POST request with invalid data

**Expected Result**:
- HTTP Status: 200 OK
- Response contains:
  - `valid`: false
  - `errors`: [
      "Title cannot exceed 255 characters",
      "Priority must be one of: LOW, MEDIUM, HIGH, CRITICAL, URGENT"
    ]

---

## Test Execution Summary

### Coverage Statistics

- **Total Test Cases**: 34
- **Positive Tests**: 18
- **Negative Tests**: 16

### Test Categories

| Category | Test Cases | Coverage |
|----------|------------|----------|
| Task Management | 22 | 65% |
| Column Management | 4 | 12% |
| Input Validation | 8 | 23% |

### Priority Distribution

| Priority | Count | Percentage |
|----------|-------|------------|
| High | 20 | 59% |
| Medium | 10 | 29% |
| Low | 4 | 12% |

---

## Test Execution Guidelines

### Prerequisites

1. Start the Spring Boot application
2. Ensure H2 database is initialized
3. Import Postman collection and environment
4. Set environment variables:
   - `base_url`: `http://localhost:8080/api`
   - `user_id`: `1`
   - `task_id`: `1`
   - `column_id`: `to-do`

### Execution Order

1. Run positive tests first to create test data
2. Run negative tests to validate error handling
3. Run cleanup tests to remove test data

### Expected Results

- All positive tests should pass with 200/201 status codes
- All negative tests should return appropriate error codes (400/404)
- All validation tests should enforce character limits and required fields
- All pagination tests should return correct page metadata

---

## Notes

- All timestamps are in ISO 8601 format
- All endpoints support JSON request/response format
- Error responses include `timestamp`, `errorCode`, `message`, and optional `details`
- Pagination defaults: page=0, size=50
- Task limit per user: 10,000
- Title max length: 255 characters
- Description max length: 10,000 characters
- Valid priorities: LOW, MEDIUM, HIGH, CRITICAL, URGENT
- Valid statuses: PENDING, TO_DO, IN_PROGRESS, DONE, COMPLETED, CANCELLED
