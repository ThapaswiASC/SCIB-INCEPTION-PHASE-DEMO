# API Test Cases Documentation

## MyProject Task Management System

**Generated:** 2024-01-15  
**Repository:** SCIB-INCEPTION-PHASE-DEMO  
**Branch:** feature/DEMO-222  
**Base URL:** http://localhost:8080/api

---

## Table of Contents

1. [Task Management Endpoints](#task-management-endpoints)
2. [Column Management Endpoints](#column-management-endpoints)
3. [Test Execution Guide](#test-execution-guide)

---

## Task Management Endpoints

### TC-001: Create Task - Valid Request

**Test Case ID:** TC-001  
**Endpoint:** POST /v1/tasks  
**Scenario:** Create a new task with valid data  
**Priority:** High

**Preconditions:**
- API server is running
- User ID exists in the system

**Request:**
```json
{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication to the API",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Steps:**
1. Send POST request to /v1/tasks with valid task data
2. Verify response status code
3. Verify response body contains task ID
4. Verify task status is set to PENDING
5. Verify task priority matches request

**Expected Result:**
- Status Code: 201 Created
- Response contains:
  - `id`: Generated task ID
  - `title`: "Implement user authentication"
  - `status`: "PENDING"
  - `priority`: "HIGH"
  - `createdAt`: Timestamp
  - `updatedAt`: Timestamp
  - `version`: 0

---

### TC-002: Create Task - Missing Title

**Test Case ID:** TC-002  
**Endpoint:** POST /v1/tasks  
**Scenario:** Attempt to create task without required title field  
**Priority:** High

**Preconditions:**
- API server is running

**Request:**
```json
{
  "userId": 1,
  "priority": "HIGH"
}
```

**Steps:**
1. Send POST request to /v1/tasks without title field
2. Verify response status code
3. Verify error response structure
4. Verify error details mention title field

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains:
  - `errorCode`: "VALIDATION_ERROR"
  - `message`: "Validation failed"
  - `details`: Array containing "title: Title is required"
  - `timestamp`: Current timestamp
  - `traceId`: UUID

---

### TC-003: Create Task - Missing User ID

**Test Case ID:** TC-003  
**Endpoint:** POST /v1/tasks  
**Scenario:** Attempt to create task without required userId field  
**Priority:** High

**Preconditions:**
- API server is running

**Request:**
```json
{
  "title": "New Task",
  "priority": "HIGH"
}
```

**Steps:**
1. Send POST request to /v1/tasks without userId field
2. Verify response status code
3. Verify error response structure

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains validation error for userId field

---

### TC-004: Create Task - Task Limit Exceeded

**Test Case ID:** TC-004  
**Endpoint:** POST /v1/tasks  
**Scenario:** Attempt to create task when user has reached maximum limit (10,000 tasks)  
**Priority:** Medium

**Preconditions:**
- User already has 10,000 tasks

**Request:**
```json
{
  "title": "New Task",
  "userId": 1,
  "priority": "HIGH"
}
```

**Steps:**
1. Send POST request to /v1/tasks
2. Verify response status code
3. Verify error message indicates limit exceeded

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "User 1 has reached the maximum task limit of 10000"

---

### TC-005: Get Task Details - Valid Task ID

**Test Case ID:** TC-005  
**Endpoint:** GET /v1/tasks/{taskId}  
**Scenario:** Retrieve details of an existing task  
**Priority:** High

**Preconditions:**
- Task with given ID exists

**Steps:**
1. Send GET request to /v1/tasks/{taskId}
2. Verify response status code
3. Verify response contains all task details

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `taskId`: Requested task ID
  - `title`: Task title
  - `status`: Current task status
  - `columnId`: Associated column ID

---

### TC-006: Get Task Details - Task Not Found

**Test Case ID:** TC-006  
**Endpoint:** GET /v1/tasks/{taskId}  
**Scenario:** Attempt to retrieve non-existent task  
**Priority:** High

**Preconditions:**
- Task with given ID does not exist

**Steps:**
1. Send GET request to /v1/tasks/99999
2. Verify response status code
3. Verify error response structure

**Expected Result:**
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "TASK_NOT_FOUND"
  - `message`: "Task not found with ID: 99999"

---

### TC-007: Update Task Status - Valid Transition

**Test Case ID:** TC-007  
**Endpoint:** PUT /v1/tasks/{taskId}/status  
**Scenario:** Update task status with valid transition (TO_DO → IN_PROGRESS)  
**Priority:** High

**Preconditions:**
- Task exists with status TO_DO

**Request:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Steps:**
1. Send PUT request to /v1/tasks/{taskId}/status
2. Verify response status code
3. Verify status updated successfully
4. Verify updatedAt timestamp is present

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `taskId`: Task ID
  - `status`: "IN_PROGRESS"
  - `updatedAt`: Current timestamp

---

### TC-008: Update Task Status - Invalid Transition

**Test Case ID:** TC-008  
**Endpoint:** PUT /v1/tasks/{taskId}/status  
**Scenario:** Attempt invalid status transition (TO_DO → DONE)  
**Priority:** High

**Preconditions:**
- Task exists with status TO_DO

**Request:**
```json
{
  "status": "DONE",
  "columnId": "done"
}
```

**Steps:**
1. Send PUT request to /v1/tasks/{taskId}/status
2. Verify response status code
3. Verify error indicates invalid transition

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains:
  - `errorCode`: "INVALID_STATUS_TRANSITION"
  - `message`: "Invalid status transition from TO_DO to DONE"

---

### TC-009: Update Task Status - Missing Status

**Test Case ID:** TC-009  
**Endpoint:** PUT /v1/tasks/{taskId}/status  
**Scenario:** Attempt to update status without providing status field  
**Priority:** Medium

**Request:**
```json
{
  "columnId": "in-progress"
}
```

**Steps:**
1. Send PUT request without status field
2. Verify validation error response

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains validation error for status field

---

### TC-010: Get User Tasks - Paginated

**Test Case ID:** TC-010  
**Endpoint:** GET /v1/users/{userId}/tasks  
**Scenario:** Retrieve paginated list of user tasks  
**Priority:** High

**Preconditions:**
- User has tasks in the system

**Query Parameters:**
- page: 0
- size: 10
- sort: createdAt,desc

**Steps:**
1. Send GET request with pagination parameters
2. Verify response status code
3. Verify pagination metadata
4. Verify content is an array

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `content`: Array of tasks
  - `page`: 0
  - `size`: 10
  - `totalElements`: Total task count
  - `totalPages`: Calculated total pages
  - `last`: Boolean indicating if last page

---

### TC-011: Get User Tasks - Empty Result

**Test Case ID:** TC-011  
**Endpoint:** GET /v1/users/{userId}/tasks  
**Scenario:** Retrieve tasks for user with no tasks  
**Priority:** Medium

**Preconditions:**
- User has no tasks

**Steps:**
1. Send GET request for user with no tasks
2. Verify response status code
3. Verify empty content array

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `content`: Empty array
  - `totalElements`: 0
  - `totalPages`: 0

---

### TC-012: Get Task Count

**Test Case ID:** TC-012  
**Endpoint:** GET /v1/users/{userId}/tasks/count  
**Scenario:** Retrieve total task count for user  
**Priority:** Medium

**Preconditions:**
- User exists in system

**Steps:**
1. Send GET request to /v1/users/{userId}/tasks/count
2. Verify response status code
3. Verify count is returned

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `userId`: Requested user ID
  - `taskCount`: Number of tasks (max 10000)

---

### TC-013: Update Task - Valid Request

**Test Case ID:** TC-013  
**Endpoint:** PUT /v1/tasks/{taskId}  
**Scenario:** Update task with valid data  
**Priority:** High

**Preconditions:**
- Task exists

**Request:**
```json
{
  "title": "Updated Task Title",
  "description": "Updated description",
  "priority": "URGENT"
}
```

**Steps:**
1. Send PUT request with updated fields
2. Verify response status code
3. Verify fields are updated

**Expected Result:**
- Status Code: 200 OK
- Response contains updated task with new values

---

### TC-014: Update Task - Task Not Found

**Test Case ID:** TC-014  
**Endpoint:** PUT /v1/tasks/{taskId}  
**Scenario:** Attempt to update non-existent task  
**Priority:** Medium

**Preconditions:**
- Task does not exist

**Steps:**
1. Send PUT request for non-existent task
2. Verify error response

**Expected Result:**
- Status Code: 404 Not Found
- Response contains TASK_NOT_FOUND error

---

### TC-015: Update Task - Title Too Long

**Test Case ID:** TC-015  
**Endpoint:** PUT /v1/tasks/{taskId}  
**Scenario:** Attempt to update task with title exceeding 255 characters  
**Priority:** Medium

**Request:**
```json
{
  "title": "<256 character string>"
}
```

**Steps:**
1. Send PUT request with oversized title
2. Verify validation error

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains validation error for title length

---

### TC-016: Delete Task - Valid Task ID

**Test Case ID:** TC-016  
**Endpoint:** DELETE /v1/tasks/{taskId}  
**Scenario:** Delete an existing task  
**Priority:** High

**Preconditions:**
- Task exists

**Steps:**
1. Send DELETE request to /v1/tasks/{taskId}
2. Verify response status code
3. Verify task is deleted

**Expected Result:**
- Status Code: 204 No Content
- No response body

---

### TC-017: Delete Task - Task Not Found

**Test Case ID:** TC-017  
**Endpoint:** DELETE /v1/tasks/{taskId}  
**Scenario:** Attempt to delete non-existent task  
**Priority:** Medium

**Preconditions:**
- Task does not exist

**Steps:**
1. Send DELETE request for non-existent task
2. Verify error response

**Expected Result:**
- Status Code: 404 Not Found
- Response contains TASK_NOT_FOUND error

---

### TC-018: Bulk Create Tasks - Valid Request

**Test Case ID:** TC-018  
**Endpoint:** POST /v1/tasks/bulk  
**Scenario:** Create multiple tasks in single request  
**Priority:** High

**Preconditions:**
- User has not reached task limit

**Request:**
```json
[
  {
    "title": "Bulk Task 1",
    "userId": 1,
    "priority": "HIGH"
  },
  {
    "title": "Bulk Task 2",
    "userId": 1,
    "priority": "MEDIUM"
  },
  {
    "title": "Bulk Task 3",
    "userId": 1,
    "priority": "LOW"
  }
]
```

**Steps:**
1. Send POST request with array of tasks
2. Verify response status code
3. Verify all tasks created successfully

**Expected Result:**
- Status Code: 201 Created
- Response contains:
  - `successCount`: 3
  - `failureCount`: 0
  - `createdTasks`: Array of 3 created tasks
  - `errors`: Empty array

---

### TC-019: Bulk Create Tasks - Exceeds Limit

**Test Case ID:** TC-019  
**Endpoint:** POST /v1/tasks/bulk  
**Scenario:** Attempt to create more than 100 tasks at once  
**Priority:** Medium

**Preconditions:**
- None

**Request:**
- Array of 101 task objects

**Steps:**
1. Send POST request with 101 tasks
2. Verify error response

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains:
  - `errorCode`: "TASK_LIMIT_EXCEEDED"
  - `message`: "Bulk creation limited to 100 tasks at a time"

---

### TC-020: Bulk Create Tasks - Partial Success

**Test Case ID:** TC-020  
**Endpoint:** POST /v1/tasks/bulk  
**Scenario:** Some tasks succeed, some fail during bulk creation  
**Priority:** Medium

**Preconditions:**
- User near task limit

**Steps:**
1. Send POST request with multiple tasks
2. Verify partial success response

**Expected Result:**
- Status Code: 201 Created
- Response contains:
  - `successCount`: Number of successful creations
  - `failureCount`: Number of failures
  - `createdTasks`: Array of successfully created tasks
  - `errors`: Array of error objects with index and message

---

## Column Management Endpoints

### TC-021: Get Column Stats - Valid Column ID

**Test Case ID:** TC-021  
**Endpoint:** GET /v1/columns/{columnId}/stats  
**Scenario:** Retrieve statistics for an existing column  
**Priority:** High

**Preconditions:**
- Column exists (to-do, in-progress, or done)

**Steps:**
1. Send GET request to /v1/columns/to-do/stats
2. Verify response status code
3. Verify response contains column statistics

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `columnId`: "to-do"
  - `taskCount`: Current task count
  - `lastUpdated`: Timestamp of last update

---

### TC-022: Get Column Stats - Column Not Found

**Test Case ID:** TC-022  
**Endpoint:** GET /v1/columns/{columnId}/stats  
**Scenario:** Attempt to retrieve stats for non-existent column  
**Priority:** Medium

**Preconditions:**
- Column does not exist

**Steps:**
1. Send GET request to /v1/columns/invalid-column/stats
2. Verify error response

**Expected Result:**
- Status Code: 404 Not Found
- Response contains:
  - `errorCode`: "COLUMN_NOT_FOUND"
  - `message`: "Column not found with ID: invalid-column"

---

### TC-023: Bulk Update Column Counts - Valid Request

**Test Case ID:** TC-023  
**Endpoint:** PUT /v1/columns/bulk-update  
**Scenario:** Update task counts for multiple columns atomically  
**Priority:** High

**Preconditions:**
- Columns exist

**Request:**
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

**Steps:**
1. Send PUT request with column updates
2. Verify response status code
3. Verify all columns updated successfully

**Expected Result:**
- Status Code: 200 OK
- Response contains:
  - `success`: true
  - `updatedColumns`: ["to-do", "in-progress"]

---

### TC-024: Bulk Update Column Counts - Missing Updates

**Test Case ID:** TC-024  
**Endpoint:** PUT /v1/columns/bulk-update  
**Scenario:** Attempt to update without providing updates array  
**Priority:** Medium

**Request:**
```json
{}
```

**Steps:**
1. Send PUT request without updates field
2. Verify validation error

**Expected Result:**
- Status Code: 400 Bad Request
- Response contains validation error for updates field

---

### TC-025: Bulk Update Column Counts - Column Not Found

**Test Case ID:** TC-025  
**Endpoint:** PUT /v1/columns/bulk-update  
**Scenario:** Attempt to update non-existent column  
**Priority:** Medium

**Request:**
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

**Steps:**
1. Send PUT request with invalid column ID
2. Verify error response

**Expected Result:**
- Status Code: 404 Not Found
- Response contains COLUMN_NOT_FOUND error

---

## Test Execution Guide

### Prerequisites

1. **Environment Setup:**
   - Java 21 installed
   - Maven 3.6+ installed
   - Application running on http://localhost:8080/api

2. **Starting the Application:**
   ```bash
   cd code
   mvn spring-boot:run
   ```

3. **Running JUnit Tests:**
   ```bash
   mvn test
   ```

4. **Running Postman Tests:**
   - Import `test/postman/collection.json`
   - Import `test/postman/environment.json`
   - Select "MyProject Task Management Environment"
   - Run collection

### Test Data

**Default Columns:**
- `to-do`: To Do (position 0)
- `in-progress`: In Progress (position 1)
- `done`: Done (position 2)

**Valid Status Transitions:**
- TO_DO → IN_PROGRESS
- IN_PROGRESS → DONE
- IN_PROGRESS → TO_DO
- PENDING → IN_PROGRESS
- IN_PROGRESS → COMPLETED
- IN_PROGRESS → CANCELLED

**Task Priorities:**
- LOW
- MEDIUM
- HIGH
- URGENT

**Task Statuses:**
- TO_DO
- IN_PROGRESS
- DONE
- PENDING
- COMPLETED
- CANCELLED

### Coverage Summary

**Total Test Cases:** 25

**By Category:**
- Task Management: 20 test cases
- Column Management: 5 test cases

**By Type:**
- Positive Tests: 13
- Negative Tests: 12

**By Priority:**
- High: 16
- Medium: 9

---

## Notes

- All timestamps are in ISO 8601 format
- All error responses include a unique traceId for debugging
- Maximum tasks per user: 10,000
- Maximum bulk creation: 100 tasks
- Maximum pagination size: 100
- Title length: 1-255 characters
- Description length: 0-2000 characters

---

**Document Version:** 1.0  
**Last Updated:** 2024-01-15