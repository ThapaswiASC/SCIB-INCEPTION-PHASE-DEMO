# API Test Cases - Task Management System

## Overview

This document describes all test cases for the Task Management System API. The system supports up to 10,000 tasks per user with a 200ms performance target for task creation.

## Test Case Summary

| Category | Positive Tests | Negative Tests | Total |
|----------|----------------|----------------|-------|
| Create Task | 1 | 4 | 5 |
| Get User Tasks | 2 | 3 | 5 |
| Get Task Count | 1 | 0 | 1 |
| Get Task by ID | 1 | 1 | 2 |
| Update Task | 1 | 2 | 3 |
| Delete Task | 1 | 1 | 2 |
| Bulk Create Tasks | 2 | 1 | 3 |
| **Total** | **9** | **12** | **21** |

---

## 1. Create Task Endpoint

### TC-001: Create Task - Valid Request

**Test Case ID:** TC-001  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Create a new task with valid data  
**Preconditions:**
- User ID exists
- User has not reached the 10,000 task limit

**Steps:**
1. Send POST request to `/v1/tasks` with valid task data
2. Include required fields: title, userId, priority
3. Optionally include description and dueDate

**Request Body:**
```json
{
  "title": "Test Task",
  "description": "Test Description",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task ID
- Response contains all submitted fields
- Task status is set to "PENDING"
- createdAt and updatedAt timestamps are set

---

### TC-002: Create Task - Missing Title

**Test Case ID:** TC-002  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task without title  
**Preconditions:** None

**Steps:**
1. Send POST request to `/v1/tasks` without title field
2. Include other required fields

**Request Body:**
```json
{
  "description": "Test Description",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates title is required
- Details array contains validation error for title field

---

### TC-003: Create Task - Title Too Long

**Test Case ID:** TC-003  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task with title exceeding 255 characters  
**Preconditions:** None

**Steps:**
1. Send POST request to `/v1/tasks` with title > 255 characters
2. Include other required fields

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates title length constraint violation

---

### TC-004: Create Task - Missing User ID

**Test Case ID:** TC-004  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task without user ID  
**Preconditions:** None

**Steps:**
1. Send POST request to `/v1/tasks` without userId field
2. Include other required fields

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates userId is required

---

### TC-005: Create Task - Task Limit Exceeded

**Test Case ID:** TC-005  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task when user has reached 10,000 task limit  
**Preconditions:**
- User already has 10,000 tasks

**Steps:**
1. Send POST request to `/v1/tasks` with valid data
2. User ID corresponds to user with 10,000 existing tasks

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "TASK_LIMIT_EXCEEDED"
- Error message indicates user has reached maximum task limit
- Details array contains limit information

---

## 2. Get User Tasks Endpoint

### TC-006: Get User Tasks - Valid Request

**Test Case ID:** TC-006  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Retrieve tasks for a user with pagination  
**Preconditions:**
- User has at least one task

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks`
2. Include query parameters: page=0, size=20

**Expected Result:**
- HTTP Status: 200 OK
- Response is an array of task objects
- Each task contains: id, title, description, userId, priority, status, createdAt, updatedAt
- Tasks are sorted by createdAt descending (newest first)
- Maximum 20 tasks returned

---

### TC-007: Get User Tasks - Default Pagination

**Test Case ID:** TC-007  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Retrieve tasks without specifying pagination parameters  
**Preconditions:**
- User exists

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks` without query parameters

**Expected Result:**
- HTTP Status: 200 OK
- Default pagination applied: page=0, size=20
- Response is an array (may be empty)

---

### TC-008: Get User Tasks - Invalid Page Number

**Test Case ID:** TC-008  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Attempt to retrieve tasks with negative page number  
**Preconditions:** None

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks?page=-1&size=20`

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error indicates invalid page parameter

---

### TC-009: Get User Tasks - Invalid Page Size (Zero)

**Test Case ID:** TC-009  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Attempt to retrieve tasks with page size of 0  
**Preconditions:** None

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks?page=0&size=0`

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error indicates invalid size parameter (minimum 1)

---

### TC-010: Get User Tasks - Page Size Exceeds Maximum

**Test Case ID:** TC-010  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Attempt to retrieve tasks with page size > 100  
**Preconditions:** None

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks?page=0&size=101`

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error indicates size parameter exceeds maximum (100)

---

## 3. Get Task Count Endpoint

### TC-011: Get Task Count - Valid Request

**Test Case ID:** TC-011  
**Endpoint:** `GET /v1/users/{userId}/tasks/count`  
**Scenario:** Retrieve task count for a user  
**Preconditions:**
- User exists

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks/count`

**Expected Result:**
- HTTP Status: 200 OK
- Response contains userId
- Response contains taskCount (integer >= 0)
- taskCount is accurate

---

## 4. Get Task by ID Endpoint

### TC-012: Get Task by ID - Valid Request

**Test Case ID:** TC-012  
**Endpoint:** `GET /v1/tasks/{taskId}`  
**Scenario:** Retrieve a specific task by ID  
**Preconditions:**
- Task with specified ID exists

**Steps:**
1. Send GET request to `/v1/tasks/{taskId}`

**Expected Result:**
- HTTP Status: 200 OK
- Response contains complete task details
- All fields are present: id, title, description, userId, priority, status, createdAt, updatedAt, dueDate

---

### TC-013: Get Task by ID - Task Not Found

**Test Case ID:** TC-013  
**Endpoint:** `GET /v1/tasks/{taskId}`  
**Scenario:** Attempt to retrieve non-existent task  
**Preconditions:**
- Task with specified ID does not exist

**Steps:**
1. Send GET request to `/v1/tasks/999999`

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "TASK_NOT_FOUND"
- Error message indicates task does not exist

---

## 5. Update Task Endpoint

### TC-014: Update Task - Valid Request

**Test Case ID:** TC-014  
**Endpoint:** `PUT /v1/tasks/{taskId}`  
**Scenario:** Update an existing task with valid data  
**Preconditions:**
- Task with specified ID exists

**Steps:**
1. Send PUT request to `/v1/tasks/{taskId}` with updated fields
2. Include any combination of: title, description, priority, status, dueDate

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "description": "Updated Description",
  "priority": "CRITICAL",
  "status": "COMPLETED"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains updated task
- Only specified fields are updated
- updatedAt timestamp is refreshed
- createdAt timestamp remains unchanged

---

### TC-015: Update Task - Task Not Found

**Test Case ID:** TC-015  
**Endpoint:** `PUT /v1/tasks/{taskId}`  
**Scenario:** Attempt to update non-existent task  
**Preconditions:**
- Task with specified ID does not exist

**Steps:**
1. Send PUT request to `/v1/tasks/999999` with valid update data

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "TASK_NOT_FOUND"
- Error message indicates task does not exist

---

### TC-016: Update Task - Title Too Long

**Test Case ID:** TC-016  
**Endpoint:** `PUT /v1/tasks/{taskId}`  
**Scenario:** Attempt to update task with title exceeding 255 characters  
**Preconditions:**
- Task with specified ID exists

**Steps:**
1. Send PUT request to `/v1/tasks/{taskId}` with title > 255 characters

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates title length constraint violation

---

## 6. Delete Task Endpoint

### TC-017: Delete Task - Valid Request

**Test Case ID:** TC-017  
**Endpoint:** `DELETE /v1/tasks/{taskId}`  
**Scenario:** Delete an existing task  
**Preconditions:**
- Task with specified ID exists

**Steps:**
1. Send DELETE request to `/v1/tasks/{taskId}`
2. Verify task is removed

**Expected Result:**
- HTTP Status: 204 No Content
- No response body
- Task is permanently deleted
- Subsequent GET request for same task returns 404

---

### TC-018: Delete Task - Task Not Found

**Test Case ID:** TC-018  
**Endpoint:** `DELETE /v1/tasks/{taskId}`  
**Scenario:** Attempt to delete non-existent task  
**Preconditions:**
- Task with specified ID does not exist

**Steps:**
1. Send DELETE request to `/v1/tasks/999999`

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "TASK_NOT_FOUND"
- Error message indicates task does not exist

---

## 7. Bulk Create Tasks Endpoint

### TC-019: Bulk Create Tasks - Valid Request

**Test Case ID:** TC-019  
**Endpoint:** `POST /v1/tasks/bulk`  
**Scenario:** Create multiple tasks in a single request  
**Preconditions:**
- User has not reached task limit
- Request contains <= 100 tasks

**Steps:**
1. Send POST request to `/v1/tasks/bulk` with array of task objects
2. Each task object contains valid data

**Request Body:**
```json
[
  {
    "title": "Bulk Task 1",
    "description": "Description 1",
    "userId": 1,
    "priority": "HIGH"
  },
  {
    "title": "Bulk Task 2",
    "description": "Description 2",
    "userId": 1,
    "priority": "MEDIUM"
  }
]
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains successCount (number of successfully created tasks)
- Response contains failureCount (number of failed tasks)
- Response contains tasks array with created task details
- Response contains errors array (empty if all succeeded)

---

### TC-020: Bulk Create Tasks - Empty List

**Test Case ID:** TC-020  
**Endpoint:** `POST /v1/tasks/bulk`  
**Scenario:** Send bulk create request with empty array  
**Preconditions:** None

**Steps:**
1. Send POST request to `/v1/tasks/bulk` with empty array `[]`

**Expected Result:**
- HTTP Status: 201 Created
- successCount: 0
- failureCount: 0
- tasks array is empty
- errors array is empty

---

### TC-021: Bulk Create Tasks - Exceeds Maximum Size

**Test Case ID:** TC-021  
**Endpoint:** `POST /v1/tasks/bulk`  
**Scenario:** Attempt to create more than 100 tasks in a single request  
**Preconditions:** None

**Steps:**
1. Send POST request to `/v1/tasks/bulk` with array containing 101 tasks

**Expected Result:**
- HTTP Status: 500 Internal Server Error (IllegalArgumentException)
- Error message indicates bulk request cannot exceed 100 tasks

---

## Test Execution Notes

### Environment Setup
- Base URL: `http://localhost:8080/api`
- Default User ID: 1
- All tests use JSON content type

### Test Data Management
- Use unique user IDs for isolation
- Clean up test data after execution
- Use environment variables for dynamic values (task_id, user_id)

### Performance Considerations
- Task creation should complete within 200ms
- Bulk operations may take longer proportionally
- Monitor performance thresholds during test execution

### Error Handling
- All error responses include: code, message, timestamp, traceId, details
- Validation errors provide field-level details
- System errors are logged with trace IDs for debugging

---

## Appendix: Priority Values

- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

## Appendix: Status Values

- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

## Appendix: Error Codes

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| TASK_LIMIT_EXCEEDED | 400 | User has reached 10,000 task limit |
| CONCURRENT_CREATION_ERROR | 409 | Concurrent task creation detected |
| PERFORMANCE_THRESHOLD_EXCEEDED | 503 | Performance threshold exceeded |
| VALIDATION_ERROR | 400 | Request validation failed |
| TASK_NOT_FOUND | 404 | Task does not exist |
| INTERNAL_SERVER_ERROR | 500 | Unexpected system error |
