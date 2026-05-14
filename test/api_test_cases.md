# API Test Cases Documentation

## Project: MyProject Task Management System

**Generated:** 2025-01-15  
**Repository:** SCIB-INCEPTION-PHASE-DEMO  
**Branch:** feature/DEMO-759  
**Base Package:** com.myproject

---

## Table of Contents

1. [Task Management API](#task-management-api)
2. [User Tasks API](#user-tasks-api)
3. [Column Management API](#column-management-api)
4. [Task Validation API](#task-validation-api)

---

## Task Management API

### TC-001: Create Task - Valid Request

**Test Case ID:** TC-001  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Create a new task with valid data  
**Priority:** High

**Preconditions:**
- User ID 1 exists
- User has not exceeded task limit (10,000 tasks)

**Request Body:**
```json
{
  "title": "Test Task",
  "description": "Task description",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task ID
- Response contains all task fields
- Task status is set to "TODO"
- Task is assigned to column "to-do"

**Validation:**
- `id` field is present and non-null
- `title` matches request
- `userId` matches request
- `status` is "TODO"
- `createdAt` and `updatedAt` timestamps are present

---

### TC-002: Create Task - Missing Title

**Test Case ID:** TC-002  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task without title  
**Priority:** High

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
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates title is required

---

### TC-003: Create Task - Empty Title

**Test Case ID:** TC-003  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task with empty title  
**Priority:** High

**Preconditions:** None

**Request Body:**
```json
{
  "title": "",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates title cannot be empty

---

### TC-004: Create Task - Whitespace Only Title

**Test Case ID:** TC-004  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task with whitespace-only title  
**Priority:** High

**Preconditions:** None

**Request Body:**
```json
{
  "title": "   ",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "INVALID_INPUT"
- Error message indicates title cannot be whitespace only

---

### TC-005: Create Task - Title Too Long

**Test Case ID:** TC-005  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task with title exceeding 255 characters  
**Priority:** Medium

**Preconditions:** None

**Request Body:**
```json
{
  "title": "<256 character string>",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message indicates title exceeds maximum length

---

### TC-006: Create Task - Task Limit Exceeded

**Test Case ID:** TC-006  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Attempt to create task when user has reached limit  
**Priority:** High

**Preconditions:**
- User has 10,000 existing tasks

**Request Body:**
```json
{
  "title": "New Task",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "TASK_LIMIT_EXCEEDED"
- Error message indicates user has reached maximum task limit

---

### TC-007: Get Task by ID - Valid

**Test Case ID:** TC-007  
**Endpoint:** `GET /v1/tasks/{taskId}`  
**Scenario:** Retrieve existing task by ID  
**Priority:** High

**Preconditions:**
- Task with ID exists

**Expected Result:**
- HTTP Status: 200 OK
- Response contains complete task details
- All fields match stored values

---

### TC-008: Get Task by ID - Not Found

**Test Case ID:** TC-008  
**Endpoint:** `GET /v1/tasks/{taskId}`  
**Scenario:** Attempt to retrieve non-existent task  
**Priority:** High

**Preconditions:** None

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "TASK_NOT_FOUND"
- Error message indicates task does not exist

---

### TC-009: Update Task - Valid

**Test Case ID:** TC-009  
**Endpoint:** `PUT /v1/tasks/{taskId}`  
**Scenario:** Update existing task with valid data  
**Priority:** High

**Preconditions:**
- Task with ID exists

**Request Body:**
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "priority": "URGENT"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains updated task
- `updatedAt` timestamp is updated
- Only provided fields are updated

---

### TC-010: Update Task - Not Found

**Test Case ID:** TC-010  
**Endpoint:** `PUT /v1/tasks/{taskId}`  
**Scenario:** Attempt to update non-existent task  
**Priority:** High

**Preconditions:** None

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "TASK_NOT_FOUND"

---

### TC-011: Update Task Status - Valid Transition

**Test Case ID:** TC-011  
**Endpoint:** `PUT /v1/tasks/{taskId}/status`  
**Scenario:** Update task status with valid transition  
**Priority:** High

**Preconditions:**
- Task exists with status "TO_DO"

**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Task status updated to "IN_PROGRESS"
- Column counts updated (old column -1, new column +1)
- `updatedAt` timestamp updated

---

### TC-012: Update Task Status - Invalid Transition

**Test Case ID:** TC-012  
**Endpoint:** `PUT /v1/tasks/{taskId}/status`  
**Scenario:** Attempt invalid status transition  
**Priority:** High

**Preconditions:**
- Task exists with status "DONE"

**Request Body:**
```json
{
  "status": "TO_DO",
  "columnId": "to-do"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "INVALID_STATUS_TRANSITION"
- Error message indicates transition is not allowed

---

### TC-013: Delete Task - Valid

**Test Case ID:** TC-013  
**Endpoint:** `DELETE /v1/tasks/{taskId}`  
**Scenario:** Delete existing task  
**Priority:** High

**Preconditions:**
- Task with ID exists

**Expected Result:**
- HTTP Status: 204 No Content
- Task is removed from datastore
- Subsequent GET returns 404

---

### TC-014: Delete Task - Not Found

**Test Case ID:** TC-014  
**Endpoint:** `DELETE /v1/tasks/{taskId}`  
**Scenario:** Attempt to delete non-existent task  
**Priority:** Medium

**Preconditions:** None

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "TASK_NOT_FOUND"

---

### TC-015: List All Tasks

**Test Case ID:** TC-015  
**Endpoint:** `GET /v1/tasks`  
**Scenario:** Retrieve all tasks  
**Priority:** Medium

**Preconditions:**
- Multiple tasks exist in system

**Expected Result:**
- HTTP Status: 200 OK
- Response is an array of tasks
- All tasks are included

---

### TC-016: Bulk Create Tasks - Valid

**Test Case ID:** TC-016  
**Endpoint:** `POST /v1/tasks/bulk`  
**Scenario:** Create multiple tasks in single request  
**Priority:** High

**Preconditions:**
- User has not exceeded task limit

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
- HTTP Status: 201 Created
- Response contains `successCount` = 2
- Response contains `failureCount` = 0
- Response contains array of created tasks

---

### TC-017: Bulk Create Tasks - Exceeds Limit

**Test Case ID:** TC-017  
**Endpoint:** `POST /v1/tasks/bulk`  
**Scenario:** Attempt to create more than 100 tasks at once  
**Priority:** Medium

**Preconditions:** None

**Request Body:**
```json
[<101 task objects>]
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "TASK_LIMIT_EXCEEDED"
- Error message indicates bulk limit exceeded

---

## User Tasks API

### TC-018: Get User Tasks - Paginated

**Test Case ID:** TC-018  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Retrieve user tasks with pagination  
**Priority:** High

**Preconditions:**
- User has multiple tasks

**Query Parameters:**
- `page=0`
- `size=50`

**Expected Result:**
- HTTP Status: 200 OK
- Response contains `content` array
- Response contains pagination metadata:
  - `totalElements`
  - `totalPages`
  - `currentPage`
  - `pageSize`
- Tasks are sorted by `createdAt` descending

---

### TC-019: Get User Tasks - Custom Page Size

**Test Case ID:** TC-019  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Retrieve user tasks with custom page size  
**Priority:** Medium

**Preconditions:**
- User has multiple tasks

**Query Parameters:**
- `page=1`
- `size=10`

**Expected Result:**
- HTTP Status: 200 OK
- Response contains correct page of results
- Page size matches request

---

### TC-020: Get User Tasks - Empty Result

**Test Case ID:** TC-020  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Retrieve tasks for user with no tasks  
**Priority:** Medium

**Preconditions:**
- User has no tasks

**Expected Result:**
- HTTP Status: 200 OK
- Response contains empty `content` array
- `totalElements` = 0
- `totalPages` = 0

---

### TC-021: Get User Task Count

**Test Case ID:** TC-021  
**Endpoint:** `GET /v1/users/{userId}/tasks/count`  
**Scenario:** Get total task count for user  
**Priority:** High

**Preconditions:**
- User has tasks

**Expected Result:**
- HTTP Status: 200 OK
- Response contains `userId`
- Response contains `taskCount`
- Count matches actual number of tasks

---

### TC-022: Get User Task Count - Zero Tasks

**Test Case ID:** TC-022  
**Endpoint:** `GET /v1/users/{userId}/tasks/count`  
**Scenario:** Get task count for user with no tasks  
**Priority:** Medium

**Preconditions:**
- User has no tasks

**Expected Result:**
- HTTP Status: 200 OK
- `taskCount` = 0

---

## Column Management API

### TC-023: Get Column Stats - Valid

**Test Case ID:** TC-023  
**Endpoint:** `GET /v1/columns/{columnId}/stats`  
**Scenario:** Retrieve statistics for existing column  
**Priority:** High

**Preconditions:**
- Column exists (e.g., "to-do")

**Expected Result:**
- HTTP Status: 200 OK
- Response contains:
  - `columnId`
  - `taskCount`
  - `lastUpdated`
- Values match current state

---

### TC-024: Get Column Stats - Not Found

**Test Case ID:** TC-024  
**Endpoint:** `GET /v1/columns/{columnId}/stats`  
**Scenario:** Attempt to get stats for non-existent column  
**Priority:** High

**Preconditions:** None

**Expected Result:**
- HTTP Status: 404 Not Found
- Error code: "COLUMN_NOT_FOUND"

---

### TC-025: Bulk Update Column Counts - Valid

**Test Case ID:** TC-025  
**Endpoint:** `PUT /v1/columns/bulk-update`  
**Scenario:** Update multiple column counts  
**Priority:** High

**Preconditions:**
- Columns exist

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
- HTTP Status: 200 OK
- Response contains `success` = true
- Response contains `updatedColumns` array
- All specified columns updated

---

### TC-026: Bulk Update Column Counts - Column Not Found

**Test Case ID:** TC-026  
**Endpoint:** `PUT /v1/columns/bulk-update`  
**Scenario:** Attempt to update non-existent column  
**Priority:** High

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
- HTTP Status: 404 Not Found
- Error code: "COLUMN_NOT_FOUND"

---

### TC-027: Bulk Update Column Counts - Missing Updates

**Test Case ID:** TC-027  
**Endpoint:** `PUT /v1/columns/bulk-update`  
**Scenario:** Send request without updates array  
**Priority:** Medium

**Preconditions:** None

**Request Body:**
```json
{}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"

---

## Task Validation API

### TC-028: Validate Task Input - Valid

**Test Case ID:** TC-028  
**Endpoint:** `POST /v1/tasks/validate`  
**Scenario:** Validate valid task input  
**Priority:** Medium

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "Valid description",
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains `valid` = true
- Response contains empty `errors` array

---

### TC-029: Validate Task Input - Invalid

**Test Case ID:** TC-029  
**Endpoint:** `POST /v1/tasks/validate`  
**Scenario:** Validate invalid task input  
**Priority:** Medium

**Preconditions:** None

**Request Body:**
```json
{
  "description": "Missing title",
  "priority": "INVALID"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Response contains validation errors

---

### TC-030: Validate Task Input - Special Characters

**Test Case ID:** TC-030  
**Endpoint:** `POST /v1/tasks/validate`  
**Scenario:** Validate task with special characters  
**Priority:** Low

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Task with @#$%^&*()",
  "description": "Description with <>&\"",
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains `valid` = true
- Special characters are accepted

---

## Error Handling Test Cases

### TC-031: Generic Exception Handling

**Test Case ID:** TC-031  
**Scenario:** Trigger unexpected server error  
**Priority:** Low

**Expected Result:**
- HTTP Status: 500 Internal Server Error
- Error code: "INTERNAL_SERVER_ERROR"
- Error message is generic (no sensitive data)
- Response includes `traceId` for debugging

---

### TC-032: Constraint Violation

**Test Case ID:** TC-032  
**Scenario:** Violate database constraints  
**Priority:** Medium

**Expected Result:**
- HTTP Status: 400 Bad Request
- Error code: "CONSTRAINT_VIOLATION"
- Error message describes constraint

---

## Performance Test Cases

### TC-033: Bulk Create - Maximum Allowed

**Test Case ID:** TC-033  
**Endpoint:** `POST /v1/tasks/bulk`  
**Scenario:** Create exactly 100 tasks (maximum)  
**Priority:** Medium

**Expected Result:**
- HTTP Status: 201 Created
- All 100 tasks created successfully
- Response time < 5 seconds

---

### TC-034: Paginated Query - Large Dataset

**Test Case ID:** TC-034  
**Endpoint:** `GET /v1/users/{userId}/tasks`  
**Scenario:** Query user with 10,000 tasks  
**Priority:** Medium

**Expected Result:**
- HTTP Status: 200 OK
- Response time < 2 seconds
- Correct pagination metadata

---

## Security Test Cases

### TC-035: CORS Headers

**Test Case ID:** TC-035  
**Scenario:** Verify CORS configuration  
**Priority:** High

**Expected Result:**
- `Access-Control-Allow-Origin` header present
- Allowed origin: `http://localhost:4200`
- All HTTP methods allowed
- Credentials enabled

---

### TC-036: Content-Type Validation

**Test Case ID:** TC-036  
**Scenario:** Send request with invalid Content-Type  
**Priority:** Medium

**Expected Result:**
- HTTP Status: 415 Unsupported Media Type
- Error message indicates expected Content-Type

---

## Edge Cases

### TC-037: Task Title - Exactly 255 Characters

**Test Case ID:** TC-037  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Create task with title at maximum length  
**Priority:** Low

**Expected Result:**
- HTTP Status: 201 Created
- Task created successfully

---

### TC-038: Task Description - Exactly 10,000 Characters

**Test Case ID:** TC-038  
**Endpoint:** `POST /v1/tasks`  
**Scenario:** Create task with description at maximum length  
**Priority:** Low

**Expected Result:**
- HTTP Status: 201 Created
- Task created successfully

---

### TC-039: Negative Column Increment

**Test Case ID:** TC-039  
**Endpoint:** `PUT /v1/columns/bulk-update`  
**Scenario:** Decrement column count below zero  
**Priority:** Medium

**Expected Result:**
- HTTP Status: 200 OK
- Column count set to 0 (not negative)

---

## Test Execution Summary

**Total Test Cases:** 39

**By Priority:**
- High: 24
- Medium: 12
- Low: 3

**By Category:**
- Task Management: 17
- User Tasks: 5
- Column Management: 5
- Task Validation: 3
- Error Handling: 2
- Performance: 2
- Security: 2
- Edge Cases: 3

**Coverage:**
- All API endpoints covered
- Positive and negative scenarios
- Validation rules
- Error handling
- Edge cases
- Performance considerations
- Security aspects

---

## Test Data Requirements

### Users
- User ID: 1 (primary test user)
- User ID: 999 (non-existent user for negative tests)

### Columns
- "to-do" (default column)
- "in-progress" (default column)
- "done" (default column)

### Tasks
- Various tasks with different statuses
- Tasks at different priority levels
- Tasks with varying description lengths

---

## Notes

1. All tests assume the application is running at `http://localhost:8080/api`
2. Tests should be executed in order for dependent scenarios
3. Database should be reset between test runs for consistency
4. Environment variables should be configured as per `environment.json`
5. All timestamps are in ISO 8601 format
6. Task IDs are auto-generated and should be captured from responses

---

**Document Version:** 1.0  
**Last Updated:** 2025-01-15  
**Maintained By:** QA Automation Team
