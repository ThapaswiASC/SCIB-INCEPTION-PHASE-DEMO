# API Test Cases Documentation

## Project: MyProject - SCIB Inception Phase Demo

**Generated:** 2024-01-15

**Base URL:** `http://localhost:8080/api`

---

## Table of Contents

1. [Task Management API](#task-management-api)
2. [Column Management API](#column-management-api)
3. [Input Validation API](#input-validation-api)

---

## Task Management API

### TC-001: Create Task - Valid Request

**Test Case ID:** TC-001

**Endpoint:** `POST /v1/tasks`

**Scenario:** Create a new task with valid input

**Preconditions:**
- User with ID 1 exists
- User has not exceeded task limit (10,000 tasks)

**Request Body:**
```json
{
  "title": "Test Task",
  "description": "This is a test task",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task ID
- Response contains title: "Test Task"
- Response contains status: "PENDING"
- Response contains priority: "HIGH"
- Response contains userId: 1
- Response contains createdAt timestamp
- Response contains updatedAt timestamp

**Validation:**
- Task is persisted in database
- User task count is incremented

---

### TC-002: Create Task - Missing Title

**Test Case ID:** TC-002

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task without title

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
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about missing title
- Response contains timestamp
- Response contains traceId

**Validation:**
- Task is not created
- User task count is not incremented

---

### TC-003: Create Task - Missing User ID

**Test Case ID:** TC-003

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task without user ID

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Test Task",
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about missing userId

---

### TC-004: Create Task - Task Limit Exceeded

**Test Case ID:** TC-004

**Endpoint:** `POST /v1/tasks`

**Scenario:** Attempt to create task when user has reached limit

**Preconditions:**
- User with ID 1 exists
- User already has 10,000 tasks

**Request Body:**
```json
{
  "title": "Test Task",
  "userId": 1,
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "TASK_LIMIT_EXCEEDED"
- Response contains error message about task limit

---

### TC-005: Get Task Details - Valid Task ID

**Test Case ID:** TC-005

**Endpoint:** `GET /v1/tasks/{taskId}`

**Scenario:** Retrieve details of an existing task

**Preconditions:**
- Task with given ID exists

**Request Parameters:**
- taskId: "1"

**Expected Result:**
- HTTP Status: 200 OK
- Response contains taskId: "1"
- Response contains title
- Response contains status
- Response contains columnId

---

### TC-006: Get Task Details - Task Not Found

**Test Case ID:** TC-006

**Endpoint:** `GET /v1/tasks/{taskId}`

**Scenario:** Attempt to retrieve non-existent task

**Preconditions:** None

**Request Parameters:**
- taskId: "999999"

**Expected Result:**
- HTTP Status: 404 Not Found
- Response contains errorCode: "TASK_NOT_FOUND"
- Response contains error message
- Response contains timestamp
- Response contains traceId

---

### TC-007: Update Task Status - Valid Transition

**Test Case ID:** TC-007

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Update task status from TO_DO to IN_PROGRESS

**Preconditions:**
- Task with given ID exists
- Task current status is TO_DO
- Column "in-progress" exists

**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains taskId
- Response contains status: "IN_PROGRESS"
- Response contains updatedAt timestamp
- Old column task count decremented
- New column task count incremented

---

### TC-008: Update Task Status - Invalid Transition

**Test Case ID:** TC-008

**Endpoint:** `PUT /v1/tasks/{taskId}/status`

**Scenario:** Attempt invalid status transition (TO_DO to DONE)

**Preconditions:**
- Task with given ID exists
- Task current status is TO_DO

**Request Body:**
```json
{
  "status": "DONE",
  "columnId": "done"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "INVALID_STATUS_TRANSITION"
- Response contains error message
- Task status remains unchanged

---

### TC-009: Update Task - Valid Request

**Test Case ID:** TC-009

**Endpoint:** `PUT /v1/tasks/{taskId}/update`

**Scenario:** Update task title and description

**Preconditions:**
- Task with given ID exists

**Request Body:**
```json
{
  "title": "Updated Task Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "URGENT"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains updated title
- Response contains updated description
- Response contains updated status
- Response contains updated priority
- Response contains updatedAt timestamp

---

### TC-010: Update Task - Title Too Long

**Test Case ID:** TC-010

**Endpoint:** `PUT /v1/tasks/{taskId}/update`

**Scenario:** Attempt to update task with title exceeding 255 characters

**Preconditions:**
- Task with given ID exists

**Request Body:**
```json
{
  "title": "<256 character string>"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about title length

---

### TC-011: Get User Tasks - Paginated

**Test Case ID:** TC-011

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve paginated list of user tasks

**Preconditions:**
- User with given ID exists
- User has at least one task

**Request Parameters:**
- userId: 1
- page: 0
- size: 50

**Expected Result:**
- HTTP Status: 200 OK
- Response contains content array
- Response contains totalElements
- Response contains totalPages
- Response contains currentPage: 0
- Response contains pageSize: 50
- Tasks are sorted by createdAt descending

---

### TC-012: Get User Tasks - Custom Pagination

**Test Case ID:** TC-012

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve second page with custom page size

**Preconditions:**
- User with given ID exists
- User has more than 10 tasks

**Request Parameters:**
- userId: 1
- page: 2
- size: 10

**Expected Result:**
- HTTP Status: 200 OK
- Response contains content array with up to 10 items
- Response contains currentPage: 2
- Response contains pageSize: 10

---

### TC-013: Get User Tasks - Empty Result

**Test Case ID:** TC-013

**Endpoint:** `GET /v1/users/{userId}/tasks`

**Scenario:** Retrieve tasks for user with no tasks

**Preconditions:**
- User with given ID exists
- User has no tasks

**Request Parameters:**
- userId: 999
- page: 0
- size: 50

**Expected Result:**
- HTTP Status: 200 OK
- Response contains empty content array
- Response contains totalElements: 0
- Response contains totalPages: 0

---

### TC-014: Get Task Count - Valid User

**Test Case ID:** TC-014

**Endpoint:** `GET /v1/users/{userId}/tasks/count`

**Scenario:** Get task count for user

**Preconditions:**
- User with given ID exists

**Request Parameters:**
- userId: 1

**Expected Result:**
- HTTP Status: 200 OK
- Response contains userId: 1
- Response contains taskCount (number)

---

### TC-015: Get Task Count - Zero Tasks

**Test Case ID:** TC-015

**Endpoint:** `GET /v1/users/{userId}/tasks/count`

**Scenario:** Get task count for user with no tasks

**Preconditions:**
- User with given ID exists
- User has no tasks

**Request Parameters:**
- userId: 999

**Expected Result:**
- HTTP Status: 200 OK
- Response contains taskCount: 0

---

### TC-016: Bulk Create Tasks - Valid Request

**Test Case ID:** TC-016

**Endpoint:** `POST /v1/tasks/bulk`

**Scenario:** Create multiple tasks in one request

**Preconditions:**
- User with ID 1 exists
- User has not exceeded task limit

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
  }
]
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains successCount: 2
- Response contains failureCount: 0
- Response contains createdTasks array with 2 items
- Response contains empty errors array

---

### TC-017: Bulk Create Tasks - Exceeds Limit

**Test Case ID:** TC-017

**Endpoint:** `POST /v1/tasks/bulk`

**Scenario:** Attempt to create more than 100 tasks at once

**Preconditions:** None

**Request Body:**
```json
[<101 task objects>]
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "TASK_LIMIT_EXCEEDED"
- Response contains error message about bulk limit

---

### TC-018: Delete Task - Valid Task ID

**Test Case ID:** TC-018

**Endpoint:** `DELETE /v1/tasks/{taskId}/delete`

**Scenario:** Delete an existing task

**Preconditions:**
- Task with given ID exists

**Request Parameters:**
- taskId: 1

**Expected Result:**
- HTTP Status: 204 No Content
- Task is removed from database
- User task count is decremented
- Column task count is decremented (if task was in a column)

---

### TC-019: Delete Task - Task Not Found

**Test Case ID:** TC-019

**Endpoint:** `DELETE /v1/tasks/{taskId}/delete`

**Scenario:** Attempt to delete non-existent task

**Preconditions:** None

**Request Parameters:**
- taskId: 999999

**Expected Result:**
- HTTP Status: 404 Not Found
- Response contains errorCode: "TASK_NOT_FOUND"
- Response contains error message

---

## Column Management API

### TC-020: Get Column Stats - Valid Column ID

**Test Case ID:** TC-020

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Retrieve statistics for a column

**Preconditions:**
- Column with given ID exists

**Request Parameters:**
- columnId: "to-do"

**Expected Result:**
- HTTP Status: 200 OK
- Response contains columnId: "to-do"
- Response contains taskCount (number)
- Response contains lastUpdated (timestamp)

---

### TC-021: Get Column Stats - Column Not Found

**Test Case ID:** TC-021

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Attempt to retrieve stats for non-existent column

**Preconditions:** None

**Request Parameters:**
- columnId: "invalid-column"

**Expected Result:**
- HTTP Status: 404 Not Found
- Response contains errorCode: "COLUMN_NOT_FOUND"
- Response contains error message

---

### TC-022: Get Column Stats - In Progress Column

**Test Case ID:** TC-022

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Retrieve statistics for in-progress column

**Preconditions:**
- Column "in-progress" exists

**Request Parameters:**
- columnId: "in-progress"

**Expected Result:**
- HTTP Status: 200 OK
- Response contains columnId: "in-progress"
- Response contains taskCount
- Response contains lastUpdated

---

### TC-023: Get Column Stats - Done Column

**Test Case ID:** TC-023

**Endpoint:** `GET /v1/columns/{columnId}/stats`

**Scenario:** Retrieve statistics for done column

**Preconditions:**
- Column "done" exists

**Request Parameters:**
- columnId: "done"

**Expected Result:**
- HTTP Status: 200 OK
- Response contains columnId: "done"
- Response contains taskCount
- Response contains lastUpdated

---

### TC-024: Bulk Update Column Counts - Valid Request

**Test Case ID:** TC-024

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Update task counts for multiple columns

**Preconditions:**
- All specified columns exist

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
- Response contains success: true
- Response contains updatedColumns array with ["to-do", "in-progress"]
- Column task counts are updated
- Column lastUpdated timestamps are updated

---

### TC-025: Bulk Update Column Counts - Single Update

**Test Case ID:** TC-025

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Update task count for single column

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
- HTTP Status: 200 OK
- Response contains success: true
- Response contains updatedColumns array with ["done"]

---

### TC-026: Bulk Update Column Counts - Column Not Found

**Test Case ID:** TC-026

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
- HTTP Status: 404 Not Found
- Response contains errorCode: "COLUMN_NOT_FOUND"
- Response contains error message

---

### TC-027: Bulk Update Column Counts - Missing Updates

**Test Case ID:** TC-027

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Attempt to update without providing updates array

**Preconditions:** None

**Request Body:**
```json
{}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about missing updates

---

### TC-028: Bulk Update Column Counts - Negative Increment

**Test Case ID:** TC-028

**Endpoint:** `PUT /v1/columns/bulk-update`

**Scenario:** Update column with negative increment

**Preconditions:**
- Column "to-do" exists

**Request Body:**
```json
{
  "updates": [
    {
      "columnId": "to-do",
      "increment": -3
    }
  ]
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains success: true
- Column task count is decremented by 3

---

## Input Validation API

### TC-029: Create Task with Validation - Valid Request

**Test Case ID:** TC-029

**Endpoint:** `POST /tasks`

**Scenario:** Create task with comprehensive validation

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Validated Task",
  "description": "Task with validation",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task ID
- Response contains sanitized title
- Response contains sanitized description
- Response contains status: "PENDING"
- Response contains priority: "HIGH"

---

### TC-030: Create Task - Empty Title

**Test Case ID:** TC-030

**Endpoint:** `POST /tasks`

**Scenario:** Attempt to create task with empty title

**Preconditions:** None

**Request Body:**
```json
{
  "title": "",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about empty title

---

### TC-031: Create Task - Whitespace Only Title

**Test Case ID:** TC-031

**Endpoint:** `POST /tasks`

**Scenario:** Attempt to create task with whitespace-only title

**Preconditions:** None

**Request Body:**
```json
{
  "title": "   ",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about whitespace-only title

---

### TC-032: Create Task - Title Too Long

**Test Case ID:** TC-032

**Endpoint:** `POST /tasks`

**Scenario:** Attempt to create task with title exceeding 255 characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "<256 character string>",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about title length (255 characters)

---

### TC-033: Create Task - Description Too Long

**Test Case ID:** TC-033

**Endpoint:** `POST /tasks`

**Scenario:** Attempt to create task with description exceeding 10,000 characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "<10001 character string>",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about description length (10,000 characters)

---

### TC-034: Create Task - Missing Priority

**Test Case ID:** TC-034

**Endpoint:** `POST /tasks`

**Scenario:** Attempt to create task without priority

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about missing priority

---

### TC-035: Create Task - Missing Status

**Test Case ID:** TC-035

**Endpoint:** `POST /tasks`

**Scenario:** Attempt to create task without status

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "priority": "HIGH"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about missing status

---

### TC-036: Create Task - Special Characters in Title

**Test Case ID:** TC-036

**Endpoint:** `POST /tasks`

**Scenario:** Create task with special characters in title

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Task with special chars: @#$%^&*()",
  "description": "Description with special chars: <>&\"",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task with sanitized special characters
- XSS-prone characters are sanitized
- Safe special characters are preserved

---

### TC-037: Create Task - Max Length Title

**Test Case ID:** TC-037

**Endpoint:** `POST /tasks`

**Scenario:** Create task with title exactly 255 characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "<exactly 255 character string>",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task with full 255-character title

---

### TC-038: Create Task - Max Length Description

**Test Case ID:** TC-038

**Endpoint:** `POST /tasks`

**Scenario:** Create task with description exactly 10,000 characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "<exactly 10000 character string>",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 201 Created
- Response contains task with full 10,000-character description

---

### TC-039: Validate Task Input - Valid Request

**Test Case ID:** TC-039

**Endpoint:** `POST /tasks/validate`

**Scenario:** Validate task input without creating task

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "Valid description",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains valid: true
- Response contains empty errors array
- Response contains empty warnings array (if present)
- No task is created

---

### TC-040: Validate Task Input - Invalid Request

**Test Case ID:** TC-040

**Endpoint:** `POST /tasks/validate`

**Scenario:** Validate invalid task input

**Preconditions:** None

**Request Body:**
```json
{
  "title": "",
  "priority": "INVALID_PRIORITY",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains valid: false
- Response contains errors array with validation messages
- Response may contain warnings array
- No task is created

---

### TC-041: Validate Task Input - Missing Title

**Test Case ID:** TC-041

**Endpoint:** `POST /tasks/validate`

**Scenario:** Validate task input without title

**Preconditions:** None

**Request Body:**
```json
{
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 400 Bad Request
- Response contains errorCode: "VALIDATION_ERROR"
- Response contains error message about missing title

---

### TC-042: Validate Task Input - Special Characters

**Test Case ID:** TC-042

**Endpoint:** `POST /tasks/validate`

**Scenario:** Validate task input with special characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Task with special chars: @#$%^&*()",
  "description": "Description with special chars: <>&\"",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains valid: true
- Response indicates special characters are acceptable
- No task is created

---

### TC-043: Validate Task Input - Max Length Title

**Test Case ID:** TC-043

**Endpoint:** `POST /tasks/validate`

**Scenario:** Validate task input with title exactly 255 characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "<exactly 255 character string>",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains valid: true
- No task is created

---

### TC-044: Validate Task Input - Max Length Description

**Test Case ID:** TC-044

**Endpoint:** `POST /tasks/validate`

**Scenario:** Validate task input with description exactly 10,000 characters

**Preconditions:** None

**Request Body:**
```json
{
  "title": "Valid Task",
  "description": "<exactly 10000 character string>",
  "priority": "HIGH",
  "status": "PENDING"
}
```

**Expected Result:**
- HTTP Status: 200 OK
- Response contains valid: true
- No task is created

---

## Test Execution Notes

### Environment Setup

1. Start the application:
   ```bash
   cd code
   mvn spring-boot:run
   ```

2. Verify application is running:
   ```bash
   curl http://localhost:8080/api/actuator/health
   ```

3. Import Postman collection and environment from `test/postman/`

### Test Data Management

- The application uses in-memory H2 database
- Data is reset on application restart
- Default user (ID: 1) is pre-initialized
- Default columns (to-do, in-progress, done) are pre-initialized

### Test Execution Order

1. Run validation tests first (TC-029 to TC-044)
2. Run task creation tests (TC-001 to TC-004)
3. Run task retrieval tests (TC-005 to TC-006)
4. Run task update tests (TC-007 to TC-010)
5. Run task listing tests (TC-011 to TC-015)
6. Run bulk operations tests (TC-016 to TC-017)
7. Run column management tests (TC-020 to TC-028)
8. Run task deletion tests (TC-018 to TC-019) last

### Known Limitations

- In-memory database means data is not persisted across restarts
- No authentication/authorization is currently enforced
- Maximum 10,000 tasks per user
- Maximum 100 tasks per bulk creation request
- Title limited to 255 characters
- Description limited to 10,000 characters

---

## Appendix

### Valid Priority Values

- LOW
- MEDIUM
- HIGH
- URGENT

### Valid Status Values (Kanban)

- TO_DO
- IN_PROGRESS
- DONE

### Valid Status Values (Validated Tasks)

- PENDING
- IN_PROGRESS
- COMPLETED
- CANCELLED

### Valid Status Transitions

- TO_DO → IN_PROGRESS
- IN_PROGRESS → DONE
- IN_PROGRESS → TO_DO
- DONE → IN_PROGRESS

### Error Codes

- TASK_NOT_FOUND: Task does not exist
- COLUMN_NOT_FOUND: Column does not exist
- USER_NOT_FOUND: User does not exist
- INVALID_STATUS_TRANSITION: Invalid task status change
- TASK_LIMIT_EXCEEDED: User has reached maximum task limit
- INVALID_INPUT: Input validation failed
- VALIDATION_ERROR: Bean validation failed
- INTERNAL_SERVER_ERROR: Unexpected error occurred

---

**Document Version:** 1.0

**Last Updated:** 2024-01-15

**Total Test Cases:** 44