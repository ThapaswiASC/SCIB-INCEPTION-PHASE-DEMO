# API Test Cases - Task Management System

## Overview

This document describes all test cases for the Task Management System API. The test cases cover positive scenarios, negative scenarios, validation errors, and edge cases.

---

## Test Case Summary

| Category | Test Cases | Status |
|----------|------------|--------|
| Task Management | 9 | ✓ |
| User Tasks | 3 | ✓ |
| Bulk Operations | 2 | ✓ |
| **Total** | **14** | **✓** |

---

## 1. Task Management

### TC-001: Create Task - Valid Request

**Endpoint:** `POST /api/v1/tasks`

**Scenario:** Create a new task with valid data

**Preconditions:**
- API server is running
- User is authenticated

**Request Body:**
```json
{
  "title": "Test Task",
  "description": "Test Description",
  "priority": "HIGH",
  "dueDate": "2025-12-31T23:59:59"
}
```

**Expected Result:**
- Status Code: `201 Created`
- Response contains task ID
- Response contains all task fields
- Task status is `PENDING`
- Task has timestamps (createdAt, updatedAt)

**Assertions:**
- `id` is not null
- `title` equals "Test Task"
- `priority` equals "HIGH"
- `status` equals "PENDING"
- `userId` equals "user123"

---

### TC-002: Create Task - Missing Title

**Endpoint:** `POST /api/v1/tasks`

**Scenario:** Attempt to create task without required title field

**Preconditions:**
- API server is running

**Request Body:**
```json
{
  "description": "Test Description",
  "priority": "HIGH"
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: `VALIDATION_ERROR`
- Error message indicates title is required
- Response contains validation details

**Assertions:**
- `errorCode` equals "VALIDATION_ERROR"
- `details` array contains title validation error

---

### TC-003: Create Task - Missing Priority

**Endpoint:** `POST /api/v1/tasks`

**Scenario:** Attempt to create task without required priority field

**Preconditions:**
- API server is running

**Request Body:**
```json
{
  "title": "Test Task",
  "description": "Test Description"
}
```

**Expected Result:**
- Status Code: `400 Bad Request`
- Error code: `VALIDATION_ERROR`
- Error message indicates priority is required

**Assertions:**
- `errorCode` equals "VALIDATION_ERROR"
- `details` array contains priority validation error

---

### TC-004: Get Task by ID - Valid

**Endpoint:** `GET /api/v1/tasks/{taskId}`

**Scenario:** Retrieve an existing task by its ID

**Preconditions:**
- Task exists in the system
- User has access to the task

**Path Parameters:**
- `taskId`: Valid UUID of existing task

**Expected Result:**
- Status Code: `200 OK`
- Response contains complete task details
- All fields are populated correctly

**Assertions:**
- `id` matches requested taskId
- `title` is not null
- `priority` is not null
- `status` is not null
- `createdAt` is not null
- `updatedAt` is not null

---

### TC-005: Get Task by ID - Not Found

**Endpoint:** `GET /api/v1/tasks/{taskId}`

**Scenario:** Attempt to retrieve a non-existent task

**Preconditions:**
- API server is running

**Path Parameters:**
- `taskId`: UUID that doesn't exist (e.g., 00000000-0000-0000-0000-000000000000)

**Expected Result:**
- Status Code: `404 Not Found`
- Error code: `TASK_NOT_FOUND`
- Error message indicates task not found

**Assertions:**
- `errorCode` equals "TASK_NOT_FOUND"
- `message` contains task ID

---

### TC-006: Update Task - Valid

**Endpoint:** `PUT /api/v1/tasks/{taskId}`

**Scenario:** Update an existing task with valid data

**Preconditions:**
- Task exists in the system
- User has access to the task

**Path Parameters:**
- `taskId`: Valid UUID of existing task

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "status": "IN_PROGRESS"
}
```

**Expected Result:**
- Status Code: `200 OK`
- Response contains updated task
- Title is updated
- Status is updated
- `updatedAt` timestamp is newer

**Assertions:**
- `title` equals "Updated Task Title"
- `status` equals "IN_PROGRESS"
- `updatedAt` is after original timestamp

---

### TC-007: Update Task - Not Found

**Endpoint:** `PUT /api/v1/tasks/{taskId}`

**Scenario:** Attempt to update a non-existent task

**Preconditions:**
- API server is running

**Path Parameters:**
- `taskId`: UUID that doesn't exist

**Request Body:**
```json
{
  "title": "Updated Task Title"
}
```

**Expected Result:**
- Status Code: `404 Not Found`
- Error code: `TASK_NOT_FOUND`

**Assertions:**
- `errorCode` equals "TASK_NOT_FOUND"

---

### TC-008: Delete Task - Valid

**Endpoint:** `DELETE /api/v1/tasks/{taskId}`

**Scenario:** Delete an existing task

**Preconditions:**
- Task exists in the system
- User has access to the task

**Path Parameters:**
- `taskId`: Valid UUID of existing task

**Expected Result:**
- Status Code: `204 No Content`
- No response body
- Task is removed from system
- Task counter is decremented

**Assertions:**
- Status code is 204
- Subsequent GET request returns 404

---

### TC-009: Delete Task - Not Found

**Endpoint:** `DELETE /api/v1/tasks/{taskId}`

**Scenario:** Attempt to delete a non-existent task

**Preconditions:**
- API server is running

**Path Parameters:**
- `taskId`: UUID that doesn't exist

**Expected Result:**
- Status Code: `404 Not Found`
- Error code: `TASK_NOT_FOUND`

**Assertions:**
- `errorCode` equals "TASK_NOT_FOUND"

---

## 2. User Tasks

### TC-010: Get User Tasks - Valid

**Endpoint:** `GET /api/v1/users/{userId}/tasks`

**Scenario:** Retrieve all tasks for a specific user with pagination

**Preconditions:**
- User exists in the system
- User has at least one task

**Path Parameters:**
- `userId`: Valid user ID

**Query Parameters:**
- `page`: 0
- `size`: 20
- `sort`: createdAt,desc

**Expected Result:**
- Status Code: `200 OK`
- Response is an array of tasks
- Tasks are sorted by creation date (descending)
- Maximum 20 tasks returned

**Assertions:**
- Response is an array
- Each task belongs to the specified user
- Tasks are sorted correctly

---

### TC-011: Get User Tasks - Default Pagination

**Endpoint:** `GET /api/v1/users/{userId}/tasks`

**Scenario:** Retrieve user tasks without specifying pagination parameters

**Preconditions:**
- User exists in the system

**Path Parameters:**
- `userId`: Valid user ID

**Expected Result:**
- Status Code: `200 OK`
- Default pagination applied (page=0, size=20)
- Response is an array

**Assertions:**
- Response is an array
- Maximum 20 tasks returned

---

### TC-012: Get Task Count - Valid

**Endpoint:** `GET /api/v1/users/{userId}/tasks/count`

**Scenario:** Retrieve task count for a specific user

**Preconditions:**
- User exists in the system

**Path Parameters:**
- `userId`: Valid user ID

**Expected Result:**
- Status Code: `200 OK`
- Response contains task count information
- Response includes remaining capacity

**Assertions:**
- `userId` matches requested user
- `taskCount` is a non-negative number
- `maxTasksAllowed` equals 10000
- `remainingCapacity` equals (maxTasksAllowed - taskCount)

---

## 3. Bulk Operations

### TC-013: Bulk Create Tasks - Valid

**Endpoint:** `POST /api/v1/tasks/bulk`

**Scenario:** Create multiple tasks in a single request

**Preconditions:**
- API server is running
- User has capacity for additional tasks

**Request Body:**
```json
[
  {
    "title": "Bulk Task 1",
    "description": "Description 1",
    "priority": "HIGH"
  },
  {
    "title": "Bulk Task 2",
    "description": "Description 2",
    "priority": "MEDIUM"
  },
  {
    "title": "Bulk Task 3",
    "description": "Description 3",
    "priority": "LOW"
  }
]
```

**Expected Result:**
- Status Code: `201 Created`
- Response contains `totalCreated` count
- Response contains array of created tasks
- Response contains array of errors (if any)

**Assertions:**
- `totalCreated` equals number of successful creations
- `tasks` array contains created task details
- `errors` array is empty (for all successful)

---

### TC-014: Bulk Create Tasks - Empty List

**Endpoint:** `POST /api/v1/tasks/bulk`

**Scenario:** Attempt bulk creation with empty array

**Preconditions:**
- API server is running

**Request Body:**
```json
[]
```

**Expected Result:**
- Status Code: `201 Created`
- `totalCreated` equals 0
- `tasks` array is empty
- `errors` array is empty

**Assertions:**
- `totalCreated` equals 0
- `tasks` is an empty array
- `errors` is an empty array

---

## Error Scenarios

### Common Error Responses

#### Validation Error (400)
```json
{
  "timestamp": 1705320000000,
  "traceId": "abc123-def456",
  "errorCode": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    "title: Title is required",
    "priority: Priority is required"
  ]
}
```

#### Task Not Found (404)
```json
{
  "timestamp": 1705320000000,
  "traceId": "abc123-def456",
  "errorCode": "TASK_NOT_FOUND",
  "message": "Task not found with id: {taskId}",
  "details": []
}
```

#### Task Limit Exceeded (400)
```json
{
  "timestamp": 1705320000000,
  "traceId": "abc123-def456",
  "errorCode": "TASK_LIMIT_EXCEEDED",
  "message": "User has reached maximum task limit of 10000",
  "details": []
}
```

#### Unauthorized (401)
```json
{
  "timestamp": 1705320000000,
  "traceId": "abc123-def456",
  "errorCode": "UNAUTHORIZED",
  "message": "Access denied to task: {taskId}",
  "details": []
}
```

---

## Test Execution

### Prerequisites
1. Start the Spring Boot application
2. Ensure the application is running on `http://localhost:8080`
3. Import the Postman collection and environment

### Running Tests

#### Using Postman
1. Import `test/postman/collection.json`
2. Import `test/postman/environment.json`
3. Select the environment
4. Run the collection

#### Using Newman (CLI)
```bash
newman run test/postman/collection.json \n  -e test/postman/environment.json \n  --reporters cli,json \n  --reporter-json-export test-results.json
```

### Expected Results
- All 14 test cases should pass
- No errors or failures
- Response times should be under 200ms for most requests

---

## Test Coverage

### Endpoints Covered
- ✓ POST /api/v1/tasks
- ✓ POST /api/v1/tasks/bulk
- ✓ GET /api/v1/tasks/{taskId}
- ✓ PUT /api/v1/tasks/{taskId}
- ✓ DELETE /api/v1/tasks/{taskId}
- ✓ GET /api/v1/users/{userId}/tasks
- ✓ GET /api/v1/users/{userId}/tasks/count

### Scenarios Covered
- ✓ Happy path (valid requests)
- ✓ Validation errors (missing/invalid fields)
- ✓ Not found errors (non-existent resources)
- ✓ Edge cases (empty lists, default values)
- ✓ Pagination
- ✓ Bulk operations

---

## Notes

1. **Authentication**: Current implementation uses mock authentication (user123). In production, replace with JWT token validation.

2. **Performance**: Task creation should complete within 200ms threshold. Monitor performance metrics during testing.

3. **Concurrency**: The system supports concurrent task creation. Consider adding load tests for concurrent scenarios.

4. **Data Persistence**: Current implementation uses in-memory storage. Data is lost on application restart.

5. **Task Limits**: Each user can create up to 10,000 tasks. Test cases should verify this limit is enforced.

---

## Maintenance

This test case document should be updated when:
- New endpoints are added
- Existing endpoints are modified
- New validation rules are introduced
- Error handling changes
- Business logic changes

---

**Last Updated:** 2025-01-15  
**Version:** 1.0.0  
**Author:** QA Automation Agent