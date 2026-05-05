# API Test Cases - MyProject Task Management System

## Overview

This document describes comprehensive API test cases for the Task Management System with Kanban board functionality. The system supports task creation, updates, deletion, status transitions, and column management.

**Base URL:** `http://localhost:8080/api`

**API Version:** v1

---

## Test Case Summary

| Category | Total Test Cases | Positive | Negative |
|----------|-----------------|----------|----------|
| Task Management | 24 | 14 | 10 |
| Column Management | 8 | 4 | 4 |
| **Total** | **32** | **18** | **14** |

---

## 1. Task Management API

### 1.1 Create Task

#### TC-TASK-001: Create Task - Valid Request

**Endpoint:** `POST /v1/tasks`

**Scenario:** Create a new task with all required fields

**Preconditions:**
- User ID 1 exists
- User has not reached task limit (10,000 tasks)

**Request Body:**
```json
{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Expected Result:**
- HTTP Status: `201 Created`
- Response contains:
  - `id`: Auto-generated task ID
  - `title`: "Implement user authentication"
  - `status`: "PENDING"
  - `priority`: "HIGH"
  - `createdAt`: Current timestamp
  - `updatedAt`: Current timestamp
  - `version`: 0

---

#### TC-TASK-002: Create Task - Missing Title

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task without required title field

**Preconditions:** None

**Request Body:**
```json
{
  "description": "Task without title",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Contains "title: Title is required"

---

#### TC-TASK-003: Create Task - Missing User ID

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task without required userId field

**Preconditions:** None

**Request Body:**
```json
{
  "title": "New Task",
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Contains "userId: User ID is required"

---

#### TC-TASK-004: Create Task - Task Limit Exceeded

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task when user has reached maximum limit (10,000 tasks)

**Preconditions:**
- User ID 1 has 10,000 existing tasks

**Request Body:**
```json
{
  "title": "New Task",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "User 1 has reached the maximum task limit of 10000"

---

### 1.2 Get Task Details

#### TC-TASK-005: Get Task Details - Valid ID

**Endpoint:** `GET /v1/tasks/{taskId}`

**Scenario:** Retrieve details of an existing task

**Preconditions:**
- Task with ID 1 exists

**Request Parameters:**
- `taskId`: 1

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `taskId`: "1"
  - `title`: Task title
  - `status`: Current task status
  - `columnId`: Associated column ID

---

#### TC-TASK-006: Get Task Details - Invalid ID

**Endpoint:** `GET /v1/tasks/{taskId}`

**Scenario:** Attempt to retrieve non-existent task

**Preconditions:** None

**Request Parameters:**
- `taskId`: 99999

**Expected Result:**
- HTTP Status: `404 Not Found`
- Error Response:
  - `errorCode`: "TASK_NOT_FOUND"
  - `message`: "Task not found with ID: 99999"

---

### 1.3 Update Task Status

#### TC-TASK-007: Update Task Status - Valid Transition (TO_DO → IN_PROGRESS)

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Update task status from TO_DO to IN_PROGRESS

**Preconditions:**
- Task with ID 1 exists
- Task current status is TO_DO

**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `taskId`: "1"
  - `status`: "IN_PROGRESS"
  - `updatedAt`: Current timestamp
- Column counts updated (to-do: -1, in-progress: +1)

---

#### TC-TASK-008: Update Task Status - Valid Transition (IN_PROGRESS → DONE)

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Update task status from IN_PROGRESS to DONE

**Preconditions:**
- Task with ID 1 exists
- Task current status is IN_PROGRESS

**Request Body:**
```json
{
  "status": "DONE",
  "columnId": "done"
}
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `taskId`: "1"
  - `status`: "DONE"
  - `updatedAt`: Current timestamp

---

#### TC-TASK-009: Update Task Status - Invalid Transition (TO_DO → DONE)

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Attempt invalid status transition from TO_DO directly to DONE

**Preconditions:**
- Task with ID 1 exists
- Task current status is TO_DO

**Request Body:**
```json
{
  "status": "DONE",
  "columnId": "done"
}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "INVALID_STATUS_TRANSITION"
  - `message`: "Invalid status transition from TO_DO to DONE"

---

#### TC-TASK-010: Update Task Status - Missing Status

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Attempt to update status without providing status field

**Preconditions:**
- Task with ID 1 exists

**Request Body:**
```json
{
  "columnId": "in-progress"
}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Contains "status: Status is required"

---

#### TC-TASK-011: Update Task Status - Task Not Found

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Attempt to update status of non-existent task

**Preconditions:** None

**Request Parameters:**
- `taskId`: 99999

**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Expected Result:**
- HTTP Status: `404 Not Found`
- Error Response:
  - `errorCode`: "TASK_NOT_FOUND"
  - `message`: "Task not found with ID: 99999"

---

### 1.4 Update Task

#### TC-TASK-012: Update Task - Valid Request

**Endpoint:** `PUT /v1/tasks/{taskId}`

**Scenario:** Update task title, description, and priority

**Preconditions:**
- Task with ID 1 exists

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "description": "Updated description",
  "priority": "URGENT"
}
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `id`: 1
  - `title`: "Updated Task Title"
  - `description`: "Updated description"
  - `priority`: "URGENT"
  - `updatedAt`: Current timestamp

---

#### TC-TASK-013: Update Task - Partial Update

**Endpoint:** `PUT /v1/tasks/{taskId}`

**Scenario:** Update only task title, leaving other fields unchanged

**Preconditions:**
- Task with ID 1 exists

**Request Body:**
```json
{
  "title": "New Title Only"
}
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `title`: "New Title Only"
  - Other fields remain unchanged

---

#### TC-TASK-014: Update Task - Title Too Long

**Endpoint:** `PUT /v1/tasks/{taskId}`

**Scenario:** Attempt to update task with title exceeding 255 characters

**Preconditions:**
- Task with ID 1 exists

**Request Body:**
```json
{
  "title": "<256 character string>"
}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Contains "title: Title must be between 1 and 255 characters"

---

#### TC-TASK-015: Update Task - Task Not Found

**Endpoint:** `PUT /v1/tasks/{taskId}`

**Scenario:** Attempt to update non-existent task

**Preconditions:** None

**Request Parameters:**
- `taskId`: 99999

**Request Body:**
```json
{
  "title": "Updated Title"
}
```

**Expected Result:**
- HTTP Status: `404 Not Found`
- Error Response:
  - `errorCode`: "TASK_NOT_FOUND"
  - `message`: "Task not found with ID: 99999"

---

### 1.5 Get User Tasks

#### TC-TASK-016: Get User Tasks - Default Pagination

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve user tasks with default pagination

**Preconditions:**
- User ID 1 has at least 1 task

**Request Parameters:**
- `userId`: 1
- `page`: 0 (default)
- `size`: 50 (default)
- `sort`: createdAt,desc (default)

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `content`: Array of tasks
  - `page`: 0
  - `size`: 50
  - `totalElements`: Total task count
  - `totalPages`: Calculated total pages
  - `last`: Boolean indicating if last page

---

#### TC-TASK-017: Get User Tasks - Custom Pagination

**Endpoint:** `GET /v1/users/{userId}/tasks?page=1&size=10&sort=title,asc`

**Scenario:** Retrieve user tasks with custom pagination parameters

**Preconditions:**
- User ID 1 has at least 11 tasks

**Request Parameters:**
- `userId`: 1
- `page`: 1
- `size`: 10
- `sort`: title,asc

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `content`: Array of 10 tasks (or fewer if last page)
  - `page`: 1
  - `size`: 10

---

#### TC-TASK-018: Get User Tasks - Empty Result

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve tasks for user with no tasks

**Preconditions:**
- User ID 999 has no tasks

**Request Parameters:**
- `userId`: 999

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `content`: Empty array
  - `totalElements`: 0
  - `totalPages`: 0

---

### 1.6 Get Task Count

#### TC-TASK-019: Get Task Count - Valid User

**Endpoint:** `GET /v1/users/{userId}/tasks/count`

**Scenario:** Retrieve task count for user with tasks

**Preconditions:**
- User ID 1 has 42 tasks

**Request Parameters:**
- `userId`: 1

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `userId`: 1
  - `taskCount`: 42

---

#### TC-TASK-020: Get Task Count - Zero Tasks

**Endpoint:** `GET /v1/users/{userId}/tasks/count`

**Scenario:** Retrieve task count for user with no tasks

**Preconditions:**
- User ID 999 has no tasks

**Request Parameters:**
- `userId`: 999

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `userId`: 999
  - `taskCount`: 0

---

### 1.7 Delete Task

#### TC-TASK-021: Delete Task - Valid ID

**Endpoint:** `DELETE /v1/tasks/{taskId}`

**Scenario:** Delete an existing task

**Preconditions:**
- Task with ID 1 exists

**Request Parameters:**
- `taskId`: 1

**Expected Result:**
- HTTP Status: `204 No Content`
- Task is removed from datastore

---

#### TC-TASK-022: Delete Task - Task Not Found

**Endpoint:** `DELETE /v1/tasks/{taskId}`

**Scenario:** Attempt to delete non-existent task

**Preconditions:** None

**Request Parameters:**
- `taskId`: 99999

**Expected Result:**
- HTTP Status: `404 Not Found`
- Error Response:
  - `errorCode`: "TASK_NOT_FOUND"
  - `message`: "Task not found with ID: 99999"

---

### 1.8 Bulk Create Tasks

#### TC-TASK-023: Bulk Create Tasks - Valid Request

**Endpoint:** `POST /v1/tasks/bulk`

**Scenario:** Create multiple tasks in a single request

**Preconditions:**
- User ID 1 has not reached task limit

**Request Body:**
```json
[
  {
    "title": "Bulk Task 1",
    "description": "First bulk task",
    "userId": 1,
    "priority": "HIGH"
  },
  {
    "title": "Bulk Task 2",
    "description": "Second bulk task",
    "userId": 1,
    "priority": "MEDIUM"
  },
  {
    "title": "Bulk Task 3",
    "description": "Third bulk task",
    "userId": 1,
    "priority": "LOW"
  }
]
```

**Expected Result:**
- HTTP Status: `201 Created`
- Response contains:
  - `successCount`: 3
  - `failureCount`: 0
  - `createdTasks`: Array of 3 created tasks
  - `errors`: Empty array

---

#### TC-TASK-024: Bulk Create Tasks - Exceeds Limit

**Endpoint:** `POST /v1/tasks/bulk`

**Scenario:** Attempt to create more than 100 tasks in a single request

**Preconditions:** None

**Request Body:**
```json
[<101 task objects>]
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "Bulk creation limited to 100 tasks at a time"

---

## 2. Column Management API

### 2.1 Get Column Statistics

#### TC-COL-001: Get Column Stats - Valid ID (to-do)

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Retrieve statistics for to-do column

**Preconditions:**
- Column "to-do" exists

**Request Parameters:**
- `columnId`: to-do

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `columnId`: "to-do"
  - `taskCount`: Current task count
  - `lastUpdated`: Timestamp of last update

---

#### TC-COL-002: Get Column Stats - Valid ID (in-progress)

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Retrieve statistics for in-progress column

**Preconditions:**
- Column "in-progress" exists

**Request Parameters:**
- `columnId`: in-progress

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `columnId`: "in-progress"
  - `taskCount`: Current task count
  - `lastUpdated`: Timestamp of last update

---

#### TC-COL-003: Get Column Stats - Valid ID (done)

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Retrieve statistics for done column

**Preconditions:**
- Column "done" exists

**Request Parameters:**
- `columnId`: done

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `columnId`: "done"
  - `taskCount`: Current task count
  - `lastUpdated`: Timestamp of last update

---

#### TC-COL-004: Get Column Stats - Invalid ID

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Attempt to retrieve statistics for non-existent column

**Preconditions:** None

**Request Parameters:**
- `columnId`: invalid-column

**Expected Result:**
- HTTP Status: `404 Not Found`
- Error Response:
  - `errorCode`: "COLUMN_NOT_FOUND"
  - `message`: "Column not found with ID: invalid-column"

---

### 2.2 Bulk Update Column Counts

#### TC-COL-005: Bulk Update Column Counts - Valid Request

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Update task counts for multiple columns atomically

**Preconditions:**
- Columns "to-do" and "in-progress" exist

**Request Body:**
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

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `success`: true
  - `updatedColumns`: ["to-do", "in-progress"]
- Column counts updated accordingly

---

#### TC-COL-006: Bulk Update Column Counts - Single Update

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Update task count for a single column

**Preconditions:**
- Column "done" exists

**Request Body:**
```json
{
  "updates": [
    {
      "columnId": "done",
      "increment": 5
    }
  ]
}
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response contains:
  - `success`: true
  - `updatedColumns`: ["done"]

---

#### TC-COL-007: Bulk Update Column Counts - Column Not Found

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Attempt to update non-existent column

**Preconditions:** None

**Request Body:**
```json
{
  "updates": [
    {
      "columnId": "invalid-column",
      "increment": 1
    }
  ]
}
```

**Expected Result:**
- HTTP Status: `404 Not Found`
- Error Response:
  - `errorCode`: "COLUMN_NOT_FOUND"
  - `message`: "Column not found with ID: invalid-column"

---

#### TC-COL-008: Bulk Update Column Counts - Missing Updates

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Attempt to update without providing updates array

**Preconditions:** None

**Request Body:**
```json
{}
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Error Response:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Contains "updates: Updates list is required"

---

## Valid Status Transitions

The following status transitions are allowed:

1. `TO_DO` → `IN_PROGRESS`
2. `IN_PROGRESS` → `DONE`
3. `IN_PROGRESS` → `TO_DO`
4. `PENDING` → `IN_PROGRESS`
5. `IN_PROGRESS` → `COMPLETED`
6. `IN_PROGRESS` → `CANCELLED`

All other transitions will result in `INVALID_STATUS_TRANSITION` error.

---

## Error Codes Reference

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| TASK_NOT_FOUND | 404 | Task does not exist |
| COLUMN_NOT_FOUND | 404 | Column does not exist |
| INVALID_STATUS_TRANSITION | 400 | Invalid status change |
| VALIDATION_ERROR | 400 | Request validation failed |
| TASK_LIMIT_EXCEEDED | 400 | User task limit reached |
| CONCURRENT_CREATION_ERROR | 409 | Concurrent modification conflict |
| PERFORMANCE_THRESHOLD_EXCEEDED | 503 | Service overloaded |
| INTERNAL_SERVER_ERROR | 500 | Unexpected error occurred |

---

## Test Execution Notes

1. **Test Order:** Execute tests in the order listed to ensure proper setup and teardown
2. **Data Cleanup:** Delete created tasks after test execution to maintain clean state
3. **Pagination:** Default page size is 50, maximum is 100
4. **Task Limits:** Maximum 10,000 tasks per user
5. **Bulk Operations:** Maximum 100 tasks per bulk create request
6. **Timestamps:** All timestamps are in ISO-8601 format
7. **Column IDs:** Pre-initialized columns are: to-do, in-progress, done

---

## Test Environment Setup

1. Start the application: `mvn spring-boot:run`
2. Application runs on: `http://localhost:8080/api`
3. Import Postman collection from: `test/postman/collection.json`
4. Import Postman environment from: `test/postman/environment.json`
5. Run tests using Postman Collection Runner or Newman CLI

---

**Document Version:** 1.0.0

**Last Updated:** 2024-01-15

**Generated By:** SpringBoot API QA Automation Agent