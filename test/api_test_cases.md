# API Test Cases - MyProject Task Management System

## Overview
This document describes all test cases for the Task Management System API.

---

## Health Check Endpoints

### TC-HEALTH-001: Get Health Status

**Endpoint:** `GET /v1/health`

**Scenario:** Verify application health status

**Preconditions:** Application is running

**Steps:**
1. Send GET request to `/v1/health`
2. Verify response status code
3. Verify response body structure

**Expected Result:**
- Status code: 200 OK
- Response body contains:
  - `status`: "UP"
  - `application`: "myproject"
  - `timestamp`: Current timestamp (number)

---

### TC-HEALTH-002: Get Performance Metrics

**Endpoint:** `GET /v1/health/metrics`

**Scenario:** Retrieve performance metrics

**Preconditions:** Application is running

**Steps:**
1. Send GET request to `/v1/health/metrics`
2. Verify response status code
3. Verify metrics are present

**Expected Result:**
- Status code: 200 OK
- Response body contains:
  - `totalTasksCreated`: Number
  - `averageCreationTimeMs`: Number
  - `timestamp`: Current timestamp

---

## Task Management Endpoints

### TC-TASK-001: Create Task - Valid Request

**Endpoint:** `POST /v1/tasks`

**Scenario:** Create a new task with valid data

**Preconditions:** User exists in the system

**Steps:**
1. Prepare valid task creation request
2. Send POST request to `/v1/tasks`
3. Verify response status code
4. Verify task is created with correct data

**Request Body:**
```json
{
  "userId": 1,
  "title": "Test Task",
  "description": "Test Description",
  "priority": "HIGH"
}
```

**Expected Result:**
- Status code: 201 Created
- Response body contains:
  - `id`: Generated task ID
  - `userId`: 1
  - `title`: "Test Task"
  - `description`: "Test Description"
  - `status`: "PENDING"
  - `priority`: "HIGH"
  - `createdAt`: Timestamp
  - `updatedAt`: Timestamp

---

### TC-TASK-002: Create Task - Missing Title

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task without required title field

**Preconditions:** None

**Steps:**
1. Prepare task creation request without title
2. Send POST request to `/v1/tasks`
3. Verify validation error response

**Request Body:**
```json
{
  "userId": 1,
  "description": "Test Description",
  "priority": "HIGH"
}
```

**Expected Result:**
- Status code: 400 Bad Request
- Response body contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Array containing "title: Title is required"

---

### TC-TASK-003: Create Task - Title Too Long

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task with title exceeding 255 characters

**Preconditions:** None

**Steps:**
1. Prepare task creation request with 256-character title
2. Send POST request to `/v1/tasks`
3. Verify validation error response

**Expected Result:**
- Status code: 400 Bad Request
- Response body contains validation error for title length

---

### TC-TASK-004: Create Task - Task Limit Exceeded

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task when user has reached 10,000 task limit

**Preconditions:** User already has 10,000 tasks

**Steps:**
1. Prepare valid task creation request
2. Send POST request to `/v1/tasks`
3. Verify task limit error response

**Expected Result:**
- Status code: 400 Bad Request
- Response body contains:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "User has reached maximum task limit of 10000"

---

### TC-TASK-005: Get User Tasks

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve all tasks for a specific user with pagination

**Preconditions:** User has at least one task

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks?page=0&size=20`
2. Verify response status code
3. Verify tasks are returned

**Expected Result:**
- Status code: 200 OK
- Response body is an array of task objects
- Each task contains: id, userId, title, description, status, priority, createdAt, updatedAt

---

### TC-TASK-006: Get User Tasks - Empty Result

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve tasks for user with no tasks

**Preconditions:** User has no tasks

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks`
2. Verify response status code
3. Verify empty array is returned

**Expected Result:**
- Status code: 200 OK
- Response body is an empty array: `[]`

---

### TC-TASK-007: Get Task Count

**Endpoint:** `GET /v1/users/{userId}/tasks/count`

**Scenario:** Get total task count for a user

**Preconditions:** User exists

**Steps:**
1. Send GET request to `/v1/users/{userId}/tasks/count`
2. Verify response status code
3. Verify count is returned

**Expected Result:**
- Status code: 200 OK
- Response body contains:
  - `userId`: User ID
  - `taskCount`: Number of tasks
  - `maxTasksAllowed`: 10000

---

### TC-TASK-008: Get Task By ID - Valid ID

**Endpoint:** `GET /v1/tasks/{taskId}`

**Scenario:** Retrieve a specific task by its ID

**Preconditions:** Task exists with given ID

**Steps:**
1. Send GET request to `/v1/tasks/{taskId}`
2. Verify response status code
3. Verify task details are returned

**Expected Result:**
- Status code: 200 OK
- Response body contains complete task details

---

### TC-TASK-009: Get Task By ID - Task Not Found

**Endpoint:** `GET /v1/tasks/{taskId}`

**Scenario:** Attempt to retrieve non-existent task

**Preconditions:** Task ID does not exist

**Steps:**
1. Send GET request to `/v1/tasks/{nonExistentId}`
2. Verify error response

**Expected Result:**
- Status code: 404 Not Found
- Response body contains:
  - `errorCode`: "RESOURCE_NOT_FOUND"
  - `message`: "Task not found with ID: {taskId}"

---

### TC-TASK-010: Update Task - Valid Request

**Endpoint:** `PUT /v1/tasks/{taskId}`

**Scenario:** Update an existing task with valid data

**Preconditions:** Task exists with given ID

**Steps:**
1. Prepare task update request
2. Send PUT request to `/v1/tasks/{taskId}`
3. Verify response status code
4. Verify task is updated

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "status": "IN_PROGRESS"
}
```

**Expected Result:**
- Status code: 200 OK
- Response body contains updated task with:
  - `title`: "Updated Task Title"
  - `status`: "IN_PROGRESS"
  - `updatedAt`: New timestamp

---

### TC-TASK-011: Update Task - Task Not Found

**Endpoint:** `PUT /v1/tasks/{taskId}`

**Scenario:** Attempt to update non-existent task

**Preconditions:** Task ID does not exist

**Steps:**
1. Prepare task update request
2. Send PUT request to `/v1/tasks/{nonExistentId}`
3. Verify error response

**Expected Result:**
- Status code: 404 Not Found
- Response body contains:
  - `errorCode`: "RESOURCE_NOT_FOUND"
  - `message`: "Task not found with ID: {taskId}"

---

### TC-TASK-012: Delete Task - Valid ID

**Endpoint:** `DELETE /v1/tasks/{taskId}`

**Scenario:** Delete an existing task

**Preconditions:** Task exists with given ID

**Steps:**
1. Send DELETE request to `/v1/tasks/{taskId}`
2. Verify response status code
3. Verify task is deleted

**Expected Result:**
- Status code: 204 No Content
- No response body
- Task is removed from system

---

### TC-TASK-013: Delete Task - Task Not Found

**Endpoint:** `DELETE /v1/tasks/{taskId}`

**Scenario:** Attempt to delete non-existent task

**Preconditions:** Task ID does not exist

**Steps:**
1. Send DELETE request to `/v1/tasks/{nonExistentId}`
2. Verify error response

**Expected Result:**
- Status code: 404 Not Found
- Response body contains:
  - `errorCode`: "RESOURCE_NOT_FOUND"
  - `message`: "Task not found with ID: {taskId}"

---

### TC-TASK-014: Bulk Create Tasks - All Successful

**Endpoint:** `POST /v1/tasks/bulk`

**Scenario:** Create multiple tasks in a single request

**Preconditions:** User exists and has not reached task limit

**Steps:**
1. Prepare array of task creation requests
2. Send POST request to `/v1/tasks/bulk`
3. Verify response status code
4. Verify all tasks are created

**Request Body:**
```json
[
  {
    "userId": 1,
    "title": "Bulk Task 1",
    "description": "Description 1",
    "priority": "HIGH"
  },
  {
    "userId": 1,
    "title": "Bulk Task 2",
    "description": "Description 2",
    "priority": "MEDIUM"
  }
]
```

**Expected Result:**
- Status code: 201 Created
- Response body contains:
  - `successCount`: 2
  - `failureCount`: 0
  - `tasks`: Array of 2 created tasks
  - `errors`: Empty array

---

### TC-TASK-015: Bulk Create Tasks - Partial Success

**Endpoint:** `POST /v1/tasks/bulk`

**Scenario:** Create multiple tasks where some fail validation

**Preconditions:** User exists

**Steps:**
1. Prepare array with valid and invalid task requests
2. Send POST request to `/v1/tasks/bulk`
3. Verify partial success response

**Expected Result:**
- Status code: 201 Created
- Response body contains:
  - `successCount`: Number of successful creations
  - `failureCount`: Number of failures
  - `tasks`: Array of successfully created tasks
  - `errors`: Array of error details for failed tasks

---

## Edge Cases

### TC-EDGE-001: Create Task Near Limit

**Scenario:** Create task when user has 9,999 tasks

**Expected Result:** Task is created successfully (9,999 < 10,000)

---

### TC-EDGE-002: Pagination - Last Page

**Scenario:** Request page beyond available data

**Expected Result:** Empty array is returned

---

### TC-EDGE-003: Update Task - Partial Update

**Scenario:** Update only specific fields, leaving others unchanged

**Expected Result:** Only specified fields are updated, others remain unchanged

---

## Performance Tests

### TC-PERF-001: Create 10,000 Tasks

**Scenario:** Create maximum allowed tasks for a single user

**Expected Result:** All tasks created successfully within performance threshold

---

### TC-PERF-002: Bulk Create 100 Tasks

**Scenario:** Create 100 tasks in a single bulk request

**Expected Result:** All tasks created successfully, average creation time < 200ms

---

## Summary

**Total Test Cases:** 20
- Health Check: 2
- Task Management: 15
- Edge Cases: 3
- Performance: 2

**Coverage:**
- Positive scenarios: 10
- Negative scenarios: 8
- Edge cases: 3
- Performance: 2