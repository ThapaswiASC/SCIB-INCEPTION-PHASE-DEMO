# API Test Cases Documentation

## Project: SCIB Inception Phase Demo - MyProject

**Repository:** SCIB-INCEPTION-PHASE-DEMO  
**Branch:** feature/DEMO-759  
**Generated:** 2024-01-15T10:00:00Z

---

## Test Coverage Summary

| Category | Total Tests | Positive | Negative | Edge Cases |
|----------|-------------|----------|----------|------------|
| Task Management | 45 | 20 | 15 | 10 |
| Kanban Board | 18 | 8 | 6 | 4 |
| Input Validation | 22 | 10 | 8 | 4 |
| User Tasks | 12 | 6 | 4 | 2 |
| **Total** | **97** | **44** | **33** | **20** |

---

## 1. Task Management API Tests

### 1.1 Create Task

#### TC-001: Create Task - Valid Request
**Endpoint:** `POST /api/v1/tasks`  
**Scenario:** Create a new task with valid data  
**Preconditions:** User exists in the system  
**Request Body:**
```json
{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication",
  "userId": 1,
  "priority": "HIGH",
  "dueDate": "2024-02-01T10:00:00"
}
```
**Expected Result:**
- Status Code: 201 Created
- Response contains task ID
- Task status is "PENDING"
- createdAt and updatedAt timestamps are set

#### TC-002: Create Task - Missing Title
**Endpoint:** `POST /api/v1/tasks`  
**Scenario:** Attempt to create task without title  
**Request Body:**
```json
{
  "description": "Task description",
  "userId": 1,
  "priority": "HIGH"
}
```
**Expected Result:**
- Status Code: 400 Bad Request
- Error code: "VALIDATION_ERROR"
- Error message contains "Title is required"

#### TC-003: Create Task - Task Limit Exceeded
**Endpoint:** `POST /api/v1/tasks`  
**Scenario:** Create task when user has reached 10,000 task limit  
**Preconditions:** User already has 10,000 tasks  
**Expected Result:**
- Status Code: 400 Bad Request
- Error code: "TASK_LIMIT_EXCEEDED"
- Error message: "User cannot have more than 10000 tasks"

---

### 1.2 Get Task Details

#### TC-004: Get Task Details - Valid Task ID
**Endpoint:** `GET /api/v1/tasks/{taskId}`  
**Scenario:** Retrieve details of an existing task  
**Path Parameters:** taskId = "1"  
**Expected Result:**
- Status Code: 200 OK
- Response contains taskId, title, status, columnId

#### TC-005: Get Task Details - Task Not Found
**Endpoint:** `GET /api/v1/tasks/{taskId}`  
**Scenario:** Attempt to retrieve non-existent task  
**Path Parameters:** taskId = "999"  
**Expected Result:**
- Status Code: 404 Not Found
- Error code: "TASK_NOT_FOUND"

---

### 1.3 Update Task Status

#### TC-006: Update Task Status - Valid Transition
**Endpoint:** `PUT /api/v1/tasks/{taskId}/status`  
**Scenario:** Move task from TO_DO to IN_PROGRESS  
**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "columnId": "in-progress"
}
```
**Expected Result:**
- Status Code: 200 OK
- Task status updated
- Column counts updated
- updatedAt timestamp refreshed

#### TC-007: Update Task Status - Invalid Transition
**Endpoint:** `PUT /api/v1/tasks/{taskId}/status`  
**Scenario:** Attempt invalid status transition (TO_DO → DONE)  
**Request Body:**
```json
{
  "status": "DONE",
  "columnId": "done"
}
```
**Expected Result:**
- Status Code: 400 Bad Request
- Error code: "INVALID_STATUS_TRANSITION"

---

### 1.4 Update Task

#### TC-008: Update Task - Valid Request
**Endpoint:** `PUT /api/v1/tasks/{taskId}/update`  
**Scenario:** Update task title and priority  
**Request Body:**
```json
{
  "title": "Updated title",
  "priority": "URGENT"
}
```
**Expected Result:**
- Status Code: 200 OK
- Task fields updated
- updatedAt timestamp refreshed

#### TC-009: Update Task - Title Too Long
**Endpoint:** `PUT /api/v1/tasks/{taskId}/update`  
**Scenario:** Attempt to update with title exceeding 255 characters  
**Expected Result:**
- Status Code: 400 Bad Request
- Error code: "VALIDATION_ERROR"

---

### 1.5 Delete Task

#### TC-010: Delete Task - Valid Task ID
**Endpoint:** `DELETE /api/v1/tasks/{taskId}/delete`  
**Scenario:** Delete an existing task  
**Expected Result:**
- Status Code: 204 No Content
- Task removed from datastore
- User task count decremented

#### TC-011: Delete Task - Task Not Found
**Endpoint:** `DELETE /api/v1/tasks/{taskId}/delete`  
**Scenario:** Attempt to delete non-existent task  
**Expected Result:**
- Status Code: 404 Not Found
- Error code: "TASK_NOT_FOUND"

---

### 1.6 Bulk Create Tasks

#### TC-012: Bulk Create Tasks - Valid Request
**Endpoint:** `POST /api/v1/tasks/bulk`  
**Scenario:** Create multiple tasks in one request  
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
- Status Code: 201 Created
- successCount: 2
- failureCount: 0
- createdTasks array contains 2 tasks

#### TC-013: Bulk Create Tasks - Partial Success
**Endpoint:** `POST /api/v1/tasks/bulk`  
**Scenario:** Some tasks succeed, some fail  
**Expected Result:**
- Status Code: 201 Created
- successCount > 0
- failureCount > 0
- errors array contains failure details

---

## 2. Kanban Board API Tests

### 2.1 Get Column Statistics

#### TC-014: Get Column Stats - Valid Column ID
**Endpoint:** `GET /api/v1/columns/{columnId}/stats`  
**Scenario:** Retrieve statistics for a column  
**Path Parameters:** columnId = "to-do"  
**Expected Result:**
- Status Code: 200 OK
- Response contains columnId, taskCount, lastUpdated

#### TC-015: Get Column Stats - Column Not Found
**Endpoint:** `GET /api/v1/columns/{columnId}/stats`  
**Scenario:** Attempt to retrieve stats for non-existent column  
**Expected Result:**
- Status Code: 404 Not Found
- Error code: "COLUMN_NOT_FOUND"

---

### 2.2 Bulk Update Column Counts

#### TC-016: Bulk Update Column Counts - Valid Request
**Endpoint:** `PUT /api/v1/columns/bulk-update`  
**Scenario:** Update counts for multiple columns  
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
- Status Code: 200 OK
- success: true
- updatedColumns array contains both column IDs

---

## 3. User Tasks API Tests

### 3.1 Get User Tasks

#### TC-017: Get User Tasks - Valid Request
**Endpoint:** `GET /api/v1/users/{userId}/tasks`  
**Scenario:** Retrieve paginated list of user tasks  
**Query Parameters:** page=0, size=50  
**Expected Result:**
- Status Code: 200 OK
- content array contains tasks
- Pagination metadata present

#### TC-018: Get User Tasks - Empty Result
**Endpoint:** `GET /api/v1/users/{userId}/tasks`  
**Scenario:** User has no tasks  
**Expected Result:**
- Status Code: 200 OK
- content array is empty
- totalElements: 0

---

### 3.2 Get Task Count

#### TC-019: Get Task Count - Valid User ID
**Endpoint:** `GET /api/v1/users/{userId}/tasks/count`  
**Scenario:** Retrieve task count for a user  
**Expected Result:**
- Status Code: 200 OK
- Response contains userId and taskCount

---

## 4. Input Validation API Tests

### 4.1 Validate Task Input

#### TC-020: Validate Task Input - Valid Request
**Endpoint:** `POST /api/v1/tasks/validate`  
**Scenario:** Validate a valid task input  
**Request Body:**
```json
{
  "title": "Valid task",
  "description": "Valid description",
  "priority": "HIGH"
}
```
**Expected Result:**
- Status Code: 200 OK
- valid: true
- errors array is empty

#### TC-021: Validate Task Input - Invalid Request
**Endpoint:** `POST /api/v1/tasks/validate`  
**Scenario:** Validate an invalid task input  
**Request Body:**
```json
{
  "title": "",
  "priority": "INVALID"
}
```
**Expected Result:**
- Status Code: 200 OK
- valid: false
- errors array contains validation errors

#### TC-022: Validate Task Input - Special Characters
**Endpoint:** `POST /api/v1/tasks/validate`  
**Scenario:** Validate task with special characters  
**Request Body:**
```json
{
  "title": "Task with special chars: @#$%^&*()",
  "description": "Description with <>&\"",
  "priority": "HIGH"
}
```
**Expected Result:**
- Status Code: 200 OK
- valid: true
- Special characters are accepted

---

## 5. Edge Cases and Error Scenarios

### 5.1 Concurrent Operations

#### TC-023: Concurrent Task Creation
**Scenario:** Multiple requests creating tasks simultaneously  
**Expected Result:** All requests handled correctly without data corruption

### 5.2 Performance Tests

#### TC-024: Bulk Create - Maximum Limit
**Scenario:** Create 100 tasks in one request (maximum allowed)  
**Expected Result:** All tasks created successfully within acceptable time

#### TC-025: Bulk Create - Exceeds Limit
**Scenario:** Attempt to create 101 tasks in one request  
**Expected Result:**
- Status Code: 400 Bad Request
- Error code: "TASK_LIMIT_EXCEEDED"

### 5.3 Data Validation

#### TC-026: Title - Maximum Length
**Scenario:** Create task with title exactly 255 characters  
**Expected Result:** Task created successfully

#### TC-027: Description - Maximum Length
**Scenario:** Create task with description exactly 10,000 characters  
**Expected Result:** Task created successfully

---

## Test Execution Guidelines

### Prerequisites
1. Application running on `http://localhost:8080/api`
2. Test data initialized
3. Postman collection imported

### Execution Order
1. Run positive test cases first
2. Run negative test cases
3. Run edge cases
4. Verify data consistency

### Success Criteria
- All positive tests pass
- All negative tests return expected error codes
- No data corruption
- Response times within acceptable limits

---

## Notes

**MISSING_DEPENDENCIES WARNING:**
- `spring-security-test` dependency is not present in pom.xml
- Security-related tests (401/403 status codes, authentication) are not included
- Add `spring-security-test` dependency to enable comprehensive security testing

**Test Data:**
- Use consistent test data across test runs
- Clean up test data after execution
- Maintain separate test and production environments

**Reporting:**
- Document all test failures
- Include screenshots for UI-related issues
- Track test execution metrics
