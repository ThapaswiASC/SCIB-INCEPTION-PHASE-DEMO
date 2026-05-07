# API Test Cases Documentation

## Project: MyProject - SCIB Inception Phase Demo

**Generated:** 2024-01-15T10:00:00Z

**Base URL:** `http://localhost:8080/api`

---

## Table of Contents

1. [Task Management API](#task-management-api)
2. [Column Management API](#column-management-api)
3. [Task Validation API](#task-validation-api)
4. [Test Coverage Summary](#test-coverage-summary)

---

## Task Management API

### TC-001: Create Task - Valid Request

**Endpoint:** `POST /v1/tasks`

**Test Case ID:** TC-001

**Scenario:** Create a new task with valid data

**Preconditions:**
- User ID 1 exists
- User has not exceeded task limit (< 10,000 tasks)

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
- Status Code: `201 Created`
- Response contains task ID
- Response contains all submitted fields
- Status is set to "PENDING"
- Created and updated timestamps are present

**Response Example:**
```json
{
  "id": 1,
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "userId": 1,
  "status": "PENDING",
  "priority": "HIGH",
  "createdAt": "2024-01-15T10:00:00",
  "updatedAt": "2024-01-15T10:00:00"
}
```

---

### TC-002: Create Task - Missing Title

**Endpoint:** `POST /v1/tasks`

**Test Case ID:** TC-002

**Scenario:** Attempt to create a task without a title

**Preconditions:** None

**Request Body:**
```json
{
  "description": "Task without title",
  "userId": 1,
  "priority": "MEDIUM"
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "VALIDATION_ERROR"
- Error message indicates title is required

**Response Example:**
```json
{
  "timestamp": "2024-01-15T10:00:00",
  "traceId": "abc-123-def",
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": ["Title is required"]
}
```

---

### TC-003: Create Task - Task Limit Exceeded

**Endpoint:** `POST /v1/tasks`

**Test Case ID:** TC-003

**Scenario:** Attempt to create a task when user has reached the 10,000 task limit

**Preconditions:**
- User ID 1 has 10,000 existing tasks

**Request Body:**
```json
{
  "title": "New task",
  "userId": 1,
  "priority": "LOW"
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "TASK_LIMIT_EXCEEDED"
- Error message indicates limit reached

---

### TC-004: Get Task by ID - Valid ID

**Endpoint:** `GET /v1/tasks/{id}`

**Test Case ID:** TC-004

**Scenario:** Retrieve an existing task by ID

**Preconditions:**
- Task with ID 1 exists

**Request:** `GET /v1/tasks/1`

**Expected Result:**
- Status Code: `200 OK`
- Response contains complete task details

---

### TC-005: Get Task by ID - Non-existent ID

**Endpoint:** `GET /v1/tasks/{id}`

**Test Case ID:** TC-005

**Scenario:** Attempt to retrieve a task that doesn't exist

**Preconditions:** None

**Request:** `GET /v1/tasks/999999`

**Expected Result:**
- Status Code: `404 Not Found`
- Error code: "TASK_NOT_FOUND"

---

### TC-006: Update Task - Valid Request

**Endpoint:** `PUT /v1/tasks/{id}`

**Test Case ID:** TC-006

**Scenario:** Update an existing task with valid data

**Preconditions:**
- Task with ID 1 exists

**Request Body:**
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "URGENT"
}
```

**Expected Result:**
- Status Code: `200 OK`
- Response contains updated fields
- Updated timestamp is newer than created timestamp

---

### TC-007: Update Task - Title Too Long

**Endpoint:** `PUT /v1/tasks/{id}`

**Test Case ID:** TC-007

**Scenario:** Attempt to update task with title exceeding 255 characters

**Preconditions:**
- Task with ID 1 exists

**Request Body:**
```json
{
  "title": "a..." (256 characters)
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "VALIDATION_ERROR"
- Error message indicates title length limit

---

### TC-008: Delete Task - Valid ID

**Endpoint:** `DELETE /v1/tasks/{id}`

**Test Case ID:** TC-008

**Scenario:** Delete an existing task

**Preconditions:**
- Task with ID 1 exists

**Request:** `DELETE /v1/tasks/1`

**Expected Result:**
- Status Code: `204 No Content`
- No response body
- Subsequent GET request returns 404

---

### TC-009: Delete Task - Non-existent ID

**Endpoint:** `DELETE /v1/tasks/{id}`

**Test Case ID:** TC-009

**Scenario:** Attempt to delete a task that doesn't exist

**Preconditions:** None

**Request:** `DELETE /v1/tasks/999999`

**Expected Result:**
- Status Code: `404 Not Found`
- Error code: "TASK_NOT_FOUND"

---

### TC-010: Get User Tasks - Valid Request

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Test Case ID:** TC-010

**Scenario:** Retrieve paginated tasks for a user

**Preconditions:**
- User ID 1 has at least 5 tasks

**Request:** `GET /v1/users/1/tasks?page=0&size=10`

**Expected Result:**
- Status Code: `200 OK`
- Response contains paginated task list
- Pagination metadata included (page, size, totalElements, totalPages)

**Response Example:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Task 1",
      "userId": 1,
      "status": "PENDING"
    }
  ],
  "currentPage": 0,
  "pageSize": 10,
  "totalElements": 5,
  "totalPages": 1
}
```

---

### TC-011: Get User Tasks - Empty Result

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Test Case ID:** TC-011

**Scenario:** Retrieve tasks for a user with no tasks

**Preconditions:**
- User ID 999 has no tasks

**Request:** `GET /v1/users/999/tasks`

**Expected Result:**
- Status Code: `200 OK`
- Empty content array
- totalElements: 0

---

### TC-012: Get Task Count - Valid User

**Endpoint:** `GET /v1/users/{userId}/tasks/count`

**Test Case ID:** TC-012

**Scenario:** Get task count for a user

**Preconditions:**
- User ID 1 has 42 tasks

**Request:** `GET /v1/users/1/tasks/count`

**Expected Result:**
- Status Code: `200 OK`
- Response contains userId and taskCount

**Response Example:**
```json
{
  "userId": 1,
  "taskCount": 42
}
```

---

### TC-013: Update Task Status - Valid Transition

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Test Case ID:** TC-013

**Scenario:** Update task status from TO_DO to IN_PROGRESS

**Preconditions:**
- Task with ID 1 exists with status TO_DO

**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Expected Result:**
- Status Code: `200 OK`
- Response contains updated status
- Updated timestamp is present

---

### TC-014: Update Task Status - Invalid Transition

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Test Case ID:** TC-014

**Scenario:** Attempt invalid status transition (TO_DO to DONE)

**Preconditions:**
- Task with ID 1 exists with status TO_DO

**Request Body:**
```json
{
  "status": "DONE",
  "columnId": "done"
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "INVALID_STATUS_TRANSITION"

---

### TC-015: Bulk Create Tasks - Valid Request

**Endpoint:** `POST /v1/tasks/bulk`

**Test Case ID:** TC-015

**Scenario:** Create multiple tasks in a single request

**Preconditions:**
- User ID 1 has not exceeded task limit

**Request Body:**
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

**Expected Result:**
- Status Code: `201 Created`
- Response contains successCount and failureCount
- createdTasks array contains all created tasks
- errors array is empty

---

### TC-016: Bulk Create Tasks - Exceeds Limit

**Endpoint:** `POST /v1/tasks/bulk`

**Test Case ID:** TC-016

**Scenario:** Attempt to create more than 100 tasks at once

**Preconditions:** None

**Request Body:** Array of 101 task creation requests

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "TASK_LIMIT_EXCEEDED"

---

## Column Management API

### TC-017: Get Column Statistics - Valid Column

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Test Case ID:** TC-017

**Scenario:** Retrieve statistics for a column

**Preconditions:**
- Column "to-do" exists

**Request:** `GET /v1/columns/to-do/stats`

**Expected Result:**
- Status Code: `200 OK`
- Response contains columnId, taskCount, lastUpdated

**Response Example:**
```json
{
  "columnId": "to-do",
  "taskCount": 15,
  "lastUpdated": "2024-01-15T10:00:00"
}
```

---

### TC-018: Get Column Statistics - Non-existent Column

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Test Case ID:** TC-018

**Scenario:** Attempt to get stats for non-existent column

**Preconditions:** None

**Request:** `GET /v1/columns/invalid-column/stats`

**Expected Result:**
- Status Code: `404 Not Found`
- Error code: "COLUMN_NOT_FOUND"

---

### TC-019: Bulk Update Column Counts - Valid Request

**Endpoint:** `PUT /v1/columns/bulk-update`

**Test Case ID:** TC-019

**Scenario:** Update task counts for multiple columns

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
- Status Code: `200 OK`
- Response contains success: true
- updatedColumns array contains both column IDs

---

### TC-020: Bulk Update Column Counts - Missing Updates

**Endpoint:** `PUT /v1/columns/bulk-update`

**Test Case ID:** TC-020

**Scenario:** Attempt bulk update without updates array

**Preconditions:** None

**Request Body:**
```json
{}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "VALIDATION_ERROR"

---

## Task Validation API

### TC-021: Validate Task Input - Valid Request

**Endpoint:** `POST /tasks/validate`

**Test Case ID:** TC-021

**Scenario:** Validate a valid task input

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "Task description",
  "priority": "HIGH"
}
```

**Expected Result:**
- Status Code: `200 OK`
- Response contains valid: true
- errors array is empty

---

### TC-022: Validate Task Input - Invalid Request

**Endpoint:** `POST /tasks/validate`

**Test Case ID:** TC-022

**Scenario:** Validate an invalid task input

**Preconditions:** None

**Request Body:**
```json
{
  "title": "",
  "priority": "INVALID_PRIORITY"
}
```

**Expected Result:**
- Status Code: `200 OK`
- Response contains valid: false
- errors array contains validation errors

---

### TC-023: Create Task with Validation - Valid Request

**Endpoint:** `POST /tasks`

**Test Case ID:** TC-023

**Scenario:** Create a task with enhanced validation

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "Task description",
  "priority": "HIGH"
}
```

**Expected Result:**
- Status Code: `201 Created`
- Response contains created task details

---

### TC-024: Create Task with Validation - Whitespace Only Title

**Endpoint:** `POST /tasks`

**Test Case ID:** TC-024

**Scenario:** Attempt to create task with whitespace-only title

**Preconditions:** None

**Request Body:**
```json
{
  "title": "   ",
  "priority": "HIGH"
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: "VALIDATION_ERROR"
- Error message indicates title cannot be whitespace only

---

## Test Coverage Summary

### Endpoints Tested

| Controller | Endpoint | Test Cases |
|------------|----------|------------|
| TaskController | POST /v1/tasks | TC-001, TC-002, TC-003 |
| TaskController | GET /v1/tasks/{id} | TC-004, TC-005 |
| TaskController | PUT /v1/tasks/{id} | TC-006, TC-007 |
| TaskController | DELETE /v1/tasks/{id} | TC-008, TC-009 |
| TaskController | GET /v1/users/{userId}/tasks | TC-010, TC-011 |
| TaskController | GET /v1/users/{userId}/tasks/count | TC-012 |
| TaskController | PUT /v1/tasks/{taskId}/status | TC-013, TC-014 |
| TaskController | POST /v1/tasks/bulk | TC-015, TC-016 |
| ColumnController | GET /v1/columns/{columnId}/stats | TC-017, TC-018 |
| ColumnController | PUT /v1/columns/bulk-update | TC-019, TC-020 |
| TaskValidationController | POST /tasks/validate | TC-021, TC-022 |
| TaskValidationController | POST /tasks | TC-023, TC-024 |

### Test Categories

- **Positive Tests:** 12 test cases
- **Negative Tests:** 12 test cases
- **Total Test Cases:** 24

### Coverage Areas

- ✅ CRUD Operations
- ✅ Input Validation
- ✅ Error Handling
- ✅ Pagination
- ✅ Bulk Operations
- ✅ Status Transitions
- ✅ Business Rules (Task Limits)
- ✅ Edge Cases

---

## Notes

1. All timestamps are in ISO 8601 format
2. All endpoints require `Content-Type: application/json` header
3. Error responses include traceId for debugging
4. Pagination defaults: page=0, size=50
5. Maximum bulk operation size: 100 items
6. Maximum tasks per user: 10,000
7. Title max length: 255 characters
8. Description max length: 10,000 characters

---

**Document Version:** 1.0

**Last Updated:** 2024-01-15T10:00:00Z